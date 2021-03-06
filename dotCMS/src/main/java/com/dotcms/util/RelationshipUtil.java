package com.dotcms.util;

import com.dotcms.contenttype.model.type.ContentType;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotValidationException;
import com.dotmarketing.business.IdentifierAPI;
import com.dotmarketing.business.RelationshipAPI;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Relationship;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.UUIDUtil;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;
import com.liferay.util.StringPool;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class used for operations over relationships
 * @author nollymar
 */
public class RelationshipUtil {

    private final static ContentletAPI contentletAPI = APILocator.getContentletAPI();

    private static final IdentifierAPI identifierAPI = APILocator.getIdentifierAPI();

    private final static RelationshipAPI relationshipAPI = APILocator.getRelationshipAPI();

    /**
     * Given a Relationship Field and a Content Type Velocity var name, returns the existing relationship
     * @param field
     * @param contentTypeVar
     * @return
     */
    public static Relationship getRelationshipFromField(final Field field, final String contentTypeVar){
        final String fieldRelationType = field.getFieldRelationType();
        return APILocator.getRelationshipAPI().byTypeValue(
                fieldRelationType.contains(StringPool.PERIOD) ? fieldRelationType
                        :contentTypeVar + StringPool.PERIOD + field
                                .getVelocityVarName());


    }

    /**
     * Returns a list of related contentlet given a comma separated list of lucene queries and/or contentlet identifiers
     * Additionally, validates the contentlets returned by the query, actually belongs to the specified relationship in the
     * given content type
     * @param relationship
     * @param contentType
     * @param language Language ID of the contentlets to be returned when filtering by identifiers
     * @param query Comma separated list of lucene queries and/or identifiers
     * @param user
     * @return
     * @throws DotDataException
     * @throws DotSecurityException
     */
    public static List<Contentlet> getRelatedContentFromQuery(
            final Relationship relationship, final ContentType contentType, final long language,
            final String query, final User user) throws DotDataException, DotSecurityException {
        List<Contentlet> relatedContentlets = Collections.EMPTY_LIST;

        if (UtilMethods.isSet(query)) {
            relatedContentlets = filterContentlet(language, query, user, true);
            validateRelatedContent(relationship, contentType, relatedContentlets);
        }

        return relatedContentlets;
    }

    /**
     * Returns a list of contentlets filtering by lucene query and/or identifiers
     * @param language
     * @param filter Comma-separated list of filtering criteria, which can be lucene queries and contentlets identifier
     * @param user
     * @return
     * @throws DotDataException
     * @throws DotSecurityException
     */
    public static List<Contentlet> filterContentlet(final long language, final String filter,
            final User user, final boolean isCheckout) throws DotDataException, DotSecurityException {

        final Map<String, Contentlet> relatedContentlets = new HashMap<>();

        //Filter can be an identifier or a lucene query (comma separated)
        for (final String elem : filter.split(StringPool.COMMA)) {
            if (UUIDUtil.isUUID(elem.trim()) && !relatedContentlets.containsKey(elem.trim())) {
                final Identifier identifier = identifierAPI.find(elem.trim());
                final Contentlet relatedContentlet = contentletAPI
                        .findContentletForLanguage(language, identifier);
                relatedContentlets.put(relatedContentlet.getIdentifier(), isCheckout ? contentletAPI
                        .checkout(relatedContentlet.getInode(), user, false) : relatedContentlet);
            } else {
                relatedContentlets
                        .putAll((isCheckout ? contentletAPI.checkoutWithQuery(elem, user, false)
                                : contentletAPI.search(elem, 0, 0, null, user, false)).stream()
                                .collect(Collectors
                                        .toMap(Contentlet::getIdentifier, Function.identity())));
            }
        }

        return relatedContentlets.values().stream().collect(CollectionsUtils.toImmutableList());
    }

    /**
     * Validates related contentlets according to the specified relationship and content type
     * @param relationship
     * @param contentType
     * @param relatedContentlets
     * @throws DotValidationException
     */
    private static void validateRelatedContent(final Relationship relationship,
            final ContentType contentType, final List<Contentlet> relatedContentlets)
            throws DotValidationException {

        //validates if the contentlet retrieved is using the correct type
        if (relationshipAPI.isParent(relationship, contentType)) {
            for (final Contentlet relatedContentlet : relatedContentlets) {
                final Structure relatedStructure = relatedContentlet.getStructure();
                if (!(relationshipAPI.isChild(relationship, relatedStructure))) {
                    throw new DotValidationException(
                            "The structure does not match the relationship" + relationship
                                    .getRelationTypeValue());
                }
            }
        }
        if (relationshipAPI.isChild(relationship, contentType)) {
            for (final Contentlet relatedContentlet : relatedContentlets) {
                final Structure relatedStructure = relatedContentlet.getStructure();
                if (!(relationshipAPI.isParent(relationship, relatedStructure))) {
                    throw new DotValidationException(
                            "The structure does not match the relationship " + relationship
                                    .getRelationTypeValue());
                }
            }
        }
    }

}
