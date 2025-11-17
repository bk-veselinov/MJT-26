package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidateJobMatchBySimilarityScoreDescThenByNameComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidateJobMatchBySimilarityScoreDescThenByTileComparator;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.CandidateSimilarityBySimilarityScoreDescThenByName;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.SkillRecommendationsByImprovementDscThenBySkillName;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators.CandidateToCandidatesMatchesGenerator;
import bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators.CandidateToJobPostingsMatchesGenerator;
import bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators.JobPostingToCandidatesMatchesGenerator;
import bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators.MatchesGenerator;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;
import bg.sofia.uni.fmi.mjt.jobmatch.statisticstrackers.MaximumValueTracker;
import bg.sofia.uni.fmi.mjt.jobmatch.statisticstrackers.SkillStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobMatch implements JobMatchAPI {

    private final Map<String, Candidate> candidates = new HashMap<>();
    private final Map<String, Employer> employers = new HashMap<>();
    private final Map<String, JobPosting> jobPostings = new HashMap<>();
    private final SkillStatistics skillStatistics = new SkillStatistics();
    private final MaximumValueTracker<JobPosting, Double> highestPaidJob = new MaximumValueTracker<>();

    @Override
    public Candidate registerCandidate(Candidate candidate) {

        if (candidate == null) {
            throw new IllegalArgumentException("Candidate is null");
        }

        if (candidates.containsKey(candidate.getEmail())) {
            throw new UserAlreadyExistsException("Candidate with the same email already exists");
        }

        candidates.put(candidate.getEmail(), candidate);
        skillStatistics.updateSkillStatistics(candidate);

        return candidate;
    }

    @Override
    public Employer registerEmployer(Employer employer) {
        if (employer == null) {
            throw new IllegalArgumentException("Employer is null");
        }

        if (employers.containsKey(employer.email())) {
            throw new UserAlreadyExistsException("Employer with the same email already exists");
        }

        employers.put(employer.email(), employer);

        return employer;
    }

    @Override
    public JobPosting postJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            throw new IllegalArgumentException("JobPosting is null");
        }

        if (!employers.containsKey(jobPosting.getEmployerEmail())) {
            throw new UserNotFoundException("Employer publishing the job posting is not registered");
        }

        if (jobPostings.containsKey(jobPosting.getId())) {
            return jobPosting;
        }

        jobPostings.put(jobPosting.getId(), jobPosting);
        skillStatistics.updateSkillStatistics(jobPosting);
        highestPaidJob.update(jobPosting, jobPosting.getSalary());

        return jobPosting;
    }

    @Override
    public List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit,
                                                            SimilarityStrategy strategy) {
        if (jobPostingId == null || jobPostingId.isBlank()) {
            throw new IllegalArgumentException("JobPostingId is null or blank");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be a positive number");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy is null");
        }

        if (!jobPostings.containsKey(jobPostingId)) {
            throw new JobPostingNotFoundException("JobPosting with this id could not be found");
        }

        JobPosting currentJobPosting = jobPostings.get(jobPostingId);
        Collection<Candidate> registeredCandidates = candidates.values();

        MatchesGenerator<JobPosting, Candidate, CandidateJobMatch> matchGenerator = new
                JobPostingToCandidatesMatchesGenerator(currentJobPosting, registeredCandidates, limit);

        return matchGenerator.generateMatchList(
                strategy,
                new CandidateJobMatchBySimilarityScoreDescThenByNameComparator()
        );
    }

    @Override
    public List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail,
                                                            int limit, SimilarityStrategy strategy) {
        if (candidateEmail == null || candidateEmail.isBlank()) {
            throw new IllegalArgumentException("CandidateEmail is null or blank");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be a positive number");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy is null");
        }

        if (!candidates.containsKey(candidateEmail)) {
            throw new CandidateNotFoundException("Candidate with this email could not be found");
        }

        Candidate currentCandidate = candidates.get(candidateEmail);
        Collection<JobPosting> postedJobs = jobPostings.values();

        MatchesGenerator<Candidate, JobPosting, CandidateJobMatch> matchGenerator = new
                CandidateToJobPostingsMatchesGenerator(currentCandidate, postedJobs, limit);

        return matchGenerator.generateMatchList(
                strategy,
                new CandidateJobMatchBySimilarityScoreDescThenByTileComparator()
        );
    }

    @Override
    public List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit,
                                                                SimilarityStrategy strategy) {
        if (candidateEmail == null || candidateEmail.isBlank()) {
            throw new IllegalArgumentException("CandidateEmail is null or blank");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be a positive number");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy is null");
        }

        if (!candidates.containsKey(candidateEmail)) {
            throw new CandidateNotFoundException("Candidate with this email could not be found");
        }

        Candidate currentCandidate = candidates.get(candidateEmail);
        Collection<Candidate> otherRegisteredCandidates = candidates.values();
        otherRegisteredCandidates.remove(currentCandidate);

        MatchesGenerator<Candidate, Candidate, CandidateSimilarityMatch> matchGenerator = new
                CandidateToCandidatesMatchesGenerator(currentCandidate, otherRegisteredCandidates, limit);

        return matchGenerator.generateMatchList(
                strategy,
                new CandidateSimilarityBySimilarityScoreDescThenByName()
        );
    }

    private static Map<JobPosting, Double> getCurrentSimilarityScores(Candidate candidate,
                                                                      Collection<JobPosting> jobPostings,
                                                                      SimilarityStrategy strategy) {
        Map<JobPosting, Double> currentSimilarities = HashMap.newHashMap(jobPostings.size());

        for (JobPosting jobPosting : jobPostings) {
            double currentSimilarity = strategy.calculateSimilarity(
                    candidate.getSkills(),
                    jobPosting.getRequiredSkills()
            );
            currentSimilarities.put(jobPosting, currentSimilarity);
        }

        return currentSimilarities;
    }

    private static double getCurrentImprovement(Collection<JobPosting> jobPostings,
                                                SimilarityStrategy strategy,
                                                Set<Skill> candidateSkills,
                                                Map<JobPosting, Double> currentSimilarities) {
        double improvement = 0;
        for (JobPosting postedJob : jobPostings) {
            double newSimilarity = strategy.calculateSimilarity(
                    candidateSkills,
                    postedJob.getRequiredSkills()
            );
            improvement += newSimilarity - currentSimilarities.get(postedJob);
        }

        return improvement;
    }

    private static List<SkillRecommendation> getSkillRecommendationList(Candidate candidate,
                                                                        Collection<JobPosting> jobPostings,
                                                                        Map<JobPosting, Double> currentSimilarities,
                                                                        SkillStatistics skillStatistics,
                                                                        SimilarityStrategy strategy,
                                                                        int limit) {
        Set<Skill> candidateSkills = new HashSet<>(candidate.getSkills());
        Set<Skill> missingSkills = skillStatistics.getAllSkills();
        missingSkills.removeAll(candidateSkills);

        if (missingSkills.isEmpty()) {
            return List.of();
        }

        List<SkillRecommendation> skillRecommendations = new ArrayList<>();

        for (Skill skill : missingSkills) {
            double totalImprovement = 0;

            Skill missingSkill = skillStatistics.getMaxSeenSkillLevel(skill);
            candidateSkills.add(missingSkill);

            totalImprovement += getCurrentImprovement(jobPostings, strategy, candidateSkills, currentSimilarities);

            if (totalImprovement > 0) {
                skillRecommendations.add(new SkillRecommendation(skill.name(), totalImprovement));
            }
            candidateSkills.remove(missingSkill);
        }

        skillRecommendations.sort(new SkillRecommendationsByImprovementDscThenBySkillName());
        limit = Math.min(limit, skillRecommendations.size());

        return List.copyOf(skillRecommendations.subList(0, limit));
    }

    @Override
    public List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail,
                                                                         int limit) {
        if (candidateEmail == null || candidateEmail.isBlank()) {
            throw new IllegalArgumentException("CandidateEmail is null or blank");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be a positive number");
        }

        if (!candidates.containsKey(candidateEmail)) {
            throw new CandidateNotFoundException("Candidate with this email was not registered");
        }

        Candidate candidate = candidates.get(candidateEmail);
        Collection<JobPosting> postedJobs = jobPostings.values();
        SimilarityStrategy strategy = new CosineSimilarity();

        Map<JobPosting, Double> currentSimilarities = getCurrentSimilarityScores(candidate, postedJobs, strategy);

        return getSkillRecommendationList(
                candidate,
                postedJobs,
                currentSimilarities,
                skillStatistics,
                strategy,
                limit
        );
    }

    @Override
    public PlatformStatistics getPlatformStatistics() {
        int totalCandidates = candidates.size();
        int totalEmployers = employers.size();
        int totalJobPostings = jobPostings.size();
        Skill mostCommonSkill = skillStatistics.getMostCommonSkill();
        String mostCommonSkillName = mostCommonSkill == null ? "" : mostCommonSkill.name();
        JobPosting highestPaidJobPosting = highestPaidJob.getKey();
        String highestPaidJobTitle = highestPaidJobPosting == null ? "" : highestPaidJobPosting.getTitle();

        return new PlatformStatistics(
                totalCandidates,
                totalEmployers,
                totalJobPostings,
                mostCommonSkillName,
                highestPaidJobTitle
        );
    }

}
