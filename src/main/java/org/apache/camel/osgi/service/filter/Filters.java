package org.apache.camel.osgi.service.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The {@code Filters} provides a simple DSL to create OSGi filters.
 */
public final class Filters {

    private Filters() {
        throw new UnsupportedOperationException("Does not supported by utility class");
    }

    /**
     * Creates the criterion that represents conjunction of all the specified criterions.
     *
     * @param f criterions to join in conjunction
     *
     * @return conjunction of all the specified criterions
     */
    public static Criterion and(Collection<? extends Criterion> f) {
        return new AndCriterion(f);
    }

    /**
     * Creates the criterion that represents conjunction of all the specified criterions.
     *
     * @param f criterions to join in conjunction
     *
     * @return conjunction of all the specified criterions
     */
    public static Criterion and(Criterion... f) {
        return new AndCriterion(f);
    }

    /**
     * Creates the criterion that represents disjunction of all the specified criterions.
     *
     * @param f criterions to join in disjunction
     *
     * @return disjunction of all the specified criterions
     */
    public static Criterion or(Collection<? extends Criterion> f) {
        return new OrCriterion(f);
    }

    /**
     * Creates the criterion that represents disjunction of all the specified criterions.
     *
     * @param f criterions to join in disjunction
     *
     * @return disjunction of all the specified criterions
     */
    public static Criterion or(Criterion... f) {
        return new OrCriterion(f);
    }

    /**
     * Creates the criterion that negates the specified one.
     *
     * @param f criterion to negate
     *
     * @return criterion negative to the specified one
     */
    public static Criterion not(Criterion f) {
        return new NotCriterion(f);
    }

    /**
     * Creates the criterion that represents equality of the specified attribute to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute for equality
     *
     * @return criterion that represents equality of the specified attribute to the specified value
     */
    public static Criterion eq(String attr, Object val) {
        return new EqCriterion(attr, val);
    }

    /**
     * Creates the criterion that represents conjunction of equalities of the map keys to the corresponding values.
     *
     * @param vals the values to create a criterion from
     *
     * @return the criterion that represents conjunction of equalities of the map keys to the corresponding values
     */
    public static Criterion allEq(Map<String, ?> vals) {
        List<Criterion> filters = new ArrayList<Criterion>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return and(filters);
    }

    /**
     * Creates the criterion that represents disjunction of equalities of the map keys to the corresponding values.
     *
     * @param vals the values to create a criterion from
     *
     * @return the criterion that represents disjunction of equalities of the map keys to the corresponding values
     */
    public static Criterion anyEq(Map<String, ?> vals) {
        List<Criterion> filters = new ArrayList<Criterion>(vals.size());
        for(Entry<String, ?> entry : vals.entrySet()) {
            filters.add(eq(entry.getKey(), entry.getValue()));
        }
        return or(filters);
    }

    /**
     * Creates the criterion that represents inequality of the specified attribute to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute for inequality
     *
     * @return criterion that represents inequality of the specified attribute to the specified value
     */
    public static Criterion ne(String attr, Object val) {
        return not(eq(attr, val));
    }

    /**
     * Creates the criterion that represents approximation of the specified attribute to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute for inequality
     *
     * @return criterion that represents approximation of the specified attribute to the specified value
     */
    public static Criterion approx(String attr, Object val) {
        return new ApproxCriterion(attr, val);
    }

    /**
     * Creates the criterion in which specified attribute must be greater or equal to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute against
     *
     * @return criterion in which specified attribute must be greater or equal to the specified value
     */
    public static Criterion ge(final String attr, final Object val) {
        return new GeCriterion(attr, val);
    }

    /**
     * Creates the criterion in which specified attribute must be less or equal to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute against
     *
     * @return criterion in which specified attribute must be less or equal to the specified value
     */
    public static Criterion le(String attr, Object val) {
        return new LeCriterion(attr, val);
    }

    /**
     * Creates the criterion that represents existence of the specified attribute.
     *
     * @param attr the name of attribute
     *
     * @return criterion that represents existence of the specified attribute
     */
    public static Criterion exists(String attr) {
        return new ExistsCriterion(attr);
    }

    /**
     * Creates the criterion in which the object, this criterion applies to, must be published into the OSGi registry
     * under a certain type.
     *
     * @param type the type of the published object to find
     *
     * @return criterion in which the object, this criterion applies to, must be published into the OSGi registry
     * under a certain type
     */
    public static Criterion is(Class<?> type) {
        return new IsCriterion(type);
    }

    /**
     * Creates the criterion in which the object, this criterion applies to, must be published into the OSGi registry
     * under a certain type.
     *
     * @param type the type of the published object to find
     *
     * @return criterion in which the object, this criterion applies to, must be published into the OSGi registry
     * under a certain type
     */
    public static Criterion is(String type) {
        return new IsCriterion(type);
    }

    /**
     * Creates the criterion that represents wildcarded match of the specified attribute to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute against
     * @param mode the {@link org.apache.camel.osgi.service.filter.LikeCriterion.MatchMode} to be used
     *
     * @return criterion that represents wildcarded match of the specified attribute to the specified value
     */
    public static Criterion like(String attr, Object val, LikeCriterion.MatchMode mode) {
        return new LikeCriterion(attr, val, mode);
    }

    /**
     * Creates the criterion that represents that uses {@link org.apache.camel.osgi.service.filter.LikeCriterion.MatchMode#EXACT}
     * to match the specified attribute to the specified value.
     *
     * @param attr the name of attribute
     * @param val the value to check the attribute against
     *
     * @return the criterion that represents that uses {@link org.apache.camel.osgi.service.filter.LikeCriterion.MatchMode#EXACT}
     * to match the specified attribute to the specified value
     */
    public static Criterion like(String attr, Object val) {
        return new LikeCriterion(attr, val);
    }

    /**
     * Creates the criterion that is completely defined by the raw representation of the OSGi filter.
     *
     * @param f the raw representation of the OSGi filter
     *
     * @return criterion that is completely defined by the raw representation of the OSGi filter
     */
    public static Criterion raw(String f) {
        return new RawCriterion(f);
    }

}
