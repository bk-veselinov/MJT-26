package bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators;

import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class MatchesGenerator<S, C, T> {

    private final S staticObject;
    private final Collection<C> collectionObjects;
    private int limit;

    protected abstract T createTObject(S staticObject, C collectionObject, double similarityScore);

    protected abstract Set<Skill> getSObjectSkills(S staticObject);

    protected abstract Set<Skill> getCObjectSkills(C collectionObject);

    protected MatchesGenerator(S staticObject, Collection<C> collectionObjects, int limit) {
        this.staticObject = staticObject;
        this.collectionObjects = collectionObjects;
        this.limit = limit;
    }

    public List<T> generateMatchList(SimilarityStrategy strategy, Comparator<T> comparator) {
        List<T> matechesList = new ArrayList<>(collectionObjects.size());

        for (C currentCollectionObject : collectionObjects) {
            double similarityScore = strategy.calculateSimilarity(
                    getCObjectSkills(currentCollectionObject),
                    getSObjectSkills(staticObject)
            );
            if (similarityScore > 0) {
                matechesList.add(createTObject(staticObject, currentCollectionObject, similarityScore));
            }

        }
        matechesList.sort(comparator);

        limit = Math.min(limit, matechesList.size());
        return List.copyOf(matechesList.subList(0, limit));
    }

}
