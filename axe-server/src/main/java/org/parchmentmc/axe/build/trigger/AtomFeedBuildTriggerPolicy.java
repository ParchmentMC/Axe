package org.parchmentmc.axe.build.trigger;

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.BaseAsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.*;
import net.mojang.manifest.versions.Manifest;
import net.mojang.manifest.versions.Version;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.parchmentmc.axe.common.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class AtomFeedBuildTriggerPolicy extends BaseAsyncPolledBuildTrigger
{

    private final BuildCustomizerFactory buildCustomizerFactory;

    public AtomFeedBuildTriggerPolicy(final BuildCustomizerFactory customizerFactory)
    {
        this.buildCustomizerFactory = customizerFactory;
    }

    private static Date getDateForRelease(final Manifest manifest, final String releaseName) {
        return manifest.getVersions().stream().filter(v -> v.getId().equals(releaseName)).map(Version::getReleaseTime).findFirst().orElse(Date.from(Instant.EPOCH));
    }

    @Nullable
    @Override
    public String triggerBuild(@Nullable final String previousValue, @NotNull final PolledTriggerContext context) throws BuildTriggerException
    {
        final String feedUrl = context.getTriggerDescriptor().getProperties().get(Constants.ATOM_TRIGGER_URL_PARAM);
        final String lastUpdatedXPath = context.getTriggerDescriptor().getProperties().get(Constants.ATOM_TRIGGER_LAST_UPDATED_PATH);
        final String newEntryXPathPattern = context.getTriggerDescriptor().getProperties().get(Constants.ATOM_TRIGGER_NEW_ENTRY_PATH);

        try(final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            final HttpGet versionsGetRequest = new HttpGet(feedUrl);
            try(final CloseableHttpResponse response = client.execute(versionsGetRequest)) {
                if (response.getStatusLine().getStatusCode() < 200 && response.getStatusLine().getStatusCode() > 299)
                {
                    throw new IllegalStateException("Could not successfully download the atom feed.");
                }

                final HttpEntity entity = response.getEntity();
                final String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

                // parse XML file
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

                XPath xPath = XPathFactory.newInstance().newXPath();
                final NodeList lastUpdateMatchingList = (NodeList) xPath.compile(lastUpdatedXPath).evaluate(xmlDocument, XPathConstants.NODESET);
                if (lastUpdateMatchingList.getLength() != 1) {
                    throw new IllegalStateException("Failed ot find the matching last updated value.");
                }
                final String lastUpdatedValue = lastUpdateMatchingList.item(0).getTextContent();
                if (Objects.equals(lastUpdatedValue, previousValue))
                    return previousValue;

                final String newEntryXPath = newEntryXPathPattern.replace("{lastUpdatedValue}", lastUpdatedValue);

                final NodeList newEntryMatchingList = (NodeList) xPath.compile(newEntryXPath).evaluate(xmlDocument, XPathConstants.NODESET);
                final List<Node> newEntries = new ArrayList<>();
                for (int i = 0; i < newEntryMatchingList.getLength(); i++) {
                    newEntries.add(newEntryMatchingList.item(i));
                }
                final List<Node> reversed = new ArrayList<>(newEntries);
                Collections.reverse(reversed);

                for (Node node : reversed) {
                    final BuildCustomizer buildCustomizer = buildCustomizerFactory.createBuildCustomizer(context.getBuildType(), null);
                    final Map<String, String> customParams = new HashMap<>();

                    final NodeList children = node.getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        final Node child = children.item(i);

                        customParams.put(jetbrains.buildServer.agent.Constants.ENV_PREFIX + getNodeName(child), getNodeValue(child));
                    }
                    buildCustomizer.setParameters(customParams);

                    final BuildPromotion buildPromotion = buildCustomizer.createPromotion();
                    buildPromotion.addToQueue(Constants.ATOM_TRIGGER_NAME);
                }

                return lastUpdatedValue;
            }
        }
        catch(Exception ex) {
            throw new BuildTriggerException("Failed to trigger build", ex);
        }
    }

    private static String getNodeName(final Node child) {
        return "ATOM_FEED_ENTRY_" + child.getNodeName().toUpperCase(Locale.ROOT);
    }

    private static String getNodeValue(final Node child) {
        if (child.hasAttributes() && child.getAttributes().getNamedItem("href") != null)
            return child.getAttributes().getNamedItem("href").getTextContent();

        final String content = child.getTextContent();

        if (content.length() >= 1997) {
            final String preTrim = content.substring(0, 1997);
            final int lastSpace = preTrim.lastIndexOf(" ");
            if (lastSpace == -1)
                return preTrim;

            final String trimmed = preTrim.substring(0, lastSpace);
            return trimmed + "...";
        }

        return content;
    }
}
