# Proto-type display for priortisation frameworks

This project is a prototype, dynamic display for a score-based prioritisation framework. What system of scoring and
summing is presented is driven from data.

Build into the solution is a database which contains:

- The score-types which are the columns for each proposed item which we are scoring by
- A set of weights are then defined for each score type
- The function which defines the score is then defined as data.

# Types of priortisation framework

There are a number of proritisation frameworks which are used, these all have their advantages and disadvantages. Some
are for early in product development and some more complex aiming to score work for systems like SaFE.

- **RICE**
: a Reasonably common framework in product management where Reach, Impact, Confidence are weighted against Effort.
- **WSJF**
: A framework used in SaFE which aims to score different aspects of development which is used for cross-departmental planning with different stakeholders, as such scoring takes longer.
- **ICE**
: Simple, but subjective, framework.  Which allows for ease of scoring.
- **Feature Buckets**
: Model for sorting features by their impact
- **Impact-Effort Matrix**
: Team orientated scoring using quadrants.

## ICE

ICE is an acromym for *Impact*, *Confidence*, and *Ease*. Each item is given a score from one to 10 with all three
numbers being added to get the final score.

### Impact

How impactful is the change or initiative.
- 1 : very low impact
- 2 - 5 : Minimal impact
- 6 - 8 Measurable impact
- 8 - 10 Significant impact

### Confidence

How confident that the feature will be well received/will provide the benefits outlined.  Measure of a risk of the time spent.  Higher confidence items are backed with data. Might include measuring risks to assess.
- 1 : Low Confidence
- 2 - 5 : Minimal confidence
- 6 - 8 : Measurable Confidence
- 9 - 10 : Significant Confidence

### Ease

How long will it take to develop

- 1 -2 : Long timeframe
- 3 - 5 : Significant timeframe
- 6 - 7 : Minimal timeframe
- 8 - 10 : Short timeframe.
