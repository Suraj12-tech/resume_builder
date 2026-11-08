# PROJECT_CONTEXT.md

## Project overview and repository audit

**Synopsis topic supplied:** “Real-Time Sentiment Analysis of Social Media Streams”.

**Repository finding:** the checked-in application is a resume-builder, not a real-time sentiment-analysis system. The README identifies the frontend as the standard React + Vite starter; the backend Maven artifact is `resumebuilderapi`. Source code, dependency manifests, routes, and data models consistently implement resume authoring and account/payment workflows. The distinction is essential: the material below supports a synopsis proposal on the supplied topic, but this repository does not currently demonstrate that topic or its claimed implementation.

### Verified technology stack

- **Frontend:** JavaScript with React 19, Vite 6, React Router 7, Tailwind CSS 4 and its Vite plugin. UI utilities include Axios, `react-hot-toast`, `react-icons`, and `lucide-react`.
- **Resume output and interaction:** `html2canvas`, `jspdf`, `react-to-print`, and Moment.js. Code implements browser-side PDF generation/download; the client also has an email-resume API path.
- **Backend:** Java 21; Spring Boot 3.5.6 with Spring Web, Spring Data MongoDB, Validation, Mail, Security, and Actuator. Other declared dependencies include Lombok, JJWT 0.11.5, Cloudinary Java SDK 1.34.0, and Razorpay Java SDK 1.4.6.
- **Persistence and external services:** MongoDB resume/user/payment document models; Cloudinary upload integration and Razorpay payment integration are represented in backend services/configuration. Actual availability depends on local service configuration and valid credentials.
- **Build/configuration:** npm scripts provide Vite dev/build/preview and ESLint linting. Maven is used for the backend. Frontend API base URL in source is `http://localhost:8080`.
- **Not present in manifests or relevant source:** Python, NLP/ML libraries, sentiment lexicons, a trained classifier, Kafka, Hadoop, Spark, Flink, stream ingestion client, or social-platform API integration.

### Existing features and architecture

The React single-page UI exposes landing, dashboard, and resume-edit routes. Forms cover profile/contact data, work history, education, skills, projects, certifications, languages, and other interests; three resume template components render resume views. Client requests use Axios and a shared API path map, with a bearer token read from local storage. PDF generation rasterizes a rendered element with html2canvas and creates an A4 PDF with jsPDF.

The Spring REST backend has controllers/services for authentication, resumes, templates, image uploads, email, and payments. Resume records are MongoDB documents containing user ownership, template/theme, profile/contact information, resume sections, and created/updated timestamps. Resume endpoints support create, list, fetch by ID, update, delete, and image upload. This is request/response CRUD; it is not an event-stream architecture.

### Data flow in the existing project

User-entered resume fields in the React forms → Axios JSON or multipart HTTP request → Spring MVC controller/service → MongoDB repository for application records (and configured external service integrations for image/payment/email operations) → JSON response → rendered resume/dashboard; rendered resume can be exported to PDF in the browser. This data flow handles resume data, not social-media posts or sentiment results.

### Implementation status for the stated sentiment topic

| Topic capability | Repository evidence/status |
|---|---|
| Social-media source/API or stream collection | Not found; no platform, credentials interface, polling/webhook client, or stream consumer is implemented. |
| Sentiment dataset or labeled examples | Not found. A dummy resume object exists, but it is unrelated to sentiment. |
| Text cleaning/tokenization/language handling | Not found for sentiment text. |
| Sentiment lexicon, statistical classifier, or neural model | Not found. No approach can be attributed to the application. |
| Queue, broker, stream processor, windowing, event-time handling | Not found. Existing HTTP calls are conventional request/response operations. |
| Sentiment REST API, database schema, dashboard, charts | Not found. Existing APIs and visualizations concern resume management. |
| Evaluation/test strategy or measured performance | No sentiment evaluation, sentiment-specific tests, metrics, or experimental results found. No result or performance claim should be reported as completed. |

The source tree includes macOS `__MACOSX` metadata artifacts and generated backend `target` files; these are not used as evidence of application features. There is no root README or project-specific sentiment documentation. The frontend README is uncustomized Vite guidance and `backend/resumebuilderapi/HELP.md` is Spring Boot-generated help. This working directory has no Git metadata, so a Git status/diff could not be used to inspect prior changes. No application files were changed for this task.

## Synopsis structure and reusable notes

### TABLE OF CONTENTS

- Introduction — Page 1
- Review of Literature — Page 6
- Objectives of the Study — Page 9
- Methodology — Page 11
- References — Page 21

### 1. Introduction

#### 1.1 Real-Time Sentiment Analysis of Social Media Streams

Sentiment analysis (also called opinion mining) computationally identifies and summarizes subjective polarity or expressed opinion in text. Depending on the task, a system may classify a post as positive, negative, or neutral; identify sentiment toward a particular target/aspect; or aggregate changes over time. Social posts are short, informal, noisy, and rapidly published, making timely classification and aggregation valuable but difficult. A real-time system adds continuous acquisition, low-latency processing, and time-aware aggregation to the NLP problem. These are background facts about the proposed subject, not functions in the inspected repository.

#### 1.2 Problem Statement

Organizations and researchers may need a timely, interpretable picture of public reactions expressed in high-volume social content. Manual review does not scale, while offline batch analysis can delay insights. A research prototype could examine how to acquire permitted social posts or a documented stream dataset, clean short informal text, classify its sentiment, aggregate results over time, and present the output with measured latency and predictive quality. The problem includes noisy language, sarcasm, context dependence, class imbalance, evolving vocabulary, source/API access limits, privacy, and the trade-off between latency, completeness, and cost.

**Repository-specific qualification:** the current code does not implement this pipeline. The problem statement motivates a proposed project and must not be worded as a problem solved by the existing resume-builder implementation.

#### 1.3 Significance of the Study

A carefully evaluated prototype could demonstrate an end-to-end method for converting permitted social text into timely aggregate sentiment indicators. Potential uses include public-opinion monitoring, customer feedback analysis, and event/reputation tracking, subject to source coverage and sampling bias. Academic significance depends on transparent data provenance, documented preprocessing and model choice, comparison against labeled ground truth, and reporting both predictive quality and system latency. Sentiment is not equivalent to public opinion: platform users are not necessarily representative of a population, and automated labels can be wrong or biased.

#### 1.5 Novelty of the Study

No repository evidence establishes a novel method, system, dataset, or result. Novelty must therefore be framed as a **proposed implementation contribution**, pending confirmation of scope and an actual working prototype. A defensible student-project contribution could be a reproducible, end-to-end demonstration that reports classifier performance and processing latency on a clearly named, legally/ethically obtained dataset or permitted source. Using standard open-source tools alone is not research novelty. A claim of methodological novelty requires a literature comparison and evidence that the design adds something beyond prior work.

### 2. Review of Literature

#### 2.1 Surveys and Conceptual Overviews of Sentiment Analysis

Sentiment analysis approaches commonly include lexicon/rule methods, supervised feature-based classifiers (for example, Naïve Bayes, logistic regression, and SVM), and neural/transformer models. Lexicon methods score words using polarity resources and can be simple to interpret, but struggle with negation, context, domain shifts, sarcasm, and unseen terms. Supervised models learn from labeled examples and require representative labeled data. Neural language models can use context more effectively but require careful domain evaluation and greater computation. These are options from the literature; none is selected or implemented in this repository.

Pang and Lee’s foundational survey describes opinion mining, subjectivity, classification, summarization, resources, and evaluation. Medhat, Hassan, and Korashy review algorithm families and applications. Tsytsarau and Palpanas survey subjective web data, opinion aggregation, trends, and related directions. Cambria and coauthors discuss the movement from keyword/heuristic approaches toward discourse-aware and concept-level analysis. These works support framing sentiment analysis as more than a simple positive/negative keyword count.

#### 2.2 Real-Time Sentiment Analysis Systems and Prototypes

Bifet and Frank study sentiment knowledge discovery in continuously generated Twitter data and discuss data-stream classification challenges. Their work proposes a sliding-window Kappa statistic for evaluation under class imbalance and changing streams. This is directly relevant to the distinction between static test-set evaluation and evolving stream evaluation.

Sakaki, Okazaki, and Matsuo’s 2010 WWW paper, “Earthquake Shakes Twitter Users: Real-Time Event Detection by Social Sensors,” uses Twitter messages and probabilistic spatiotemporal treatment to detect earthquake-related events. It is an example of real-time social sensing/event detection, not evidence that an arbitrary sentiment classifier or the current repository supports live platform access.

Abbasi, Hassan, and Dhar compare 20 Twitter sentiment-analysis tools over five test beds and report benchmark results and error analysis. This motivates evaluating models on explicit test data rather than assuming a tool’s quality. SemEval Twitter sentiment tasks further illustrate shared task definitions such as polarity classification and sentiment quantification.

#### 2.3 Big Data Pipelines and Stream Processing Frameworks

Stream-processing literature treats an input as potentially unbounded and emphasizes event time, windows, out-of-order events, state, correctness, latency, and cost. Akidau et al.’s Dataflow model describes trade-offs among correctness, latency, and cost for unbounded, unordered data. Carbone et al. describe Apache Flink as a stream and batch processing engine. Bifet et al.’s MOA paper presents a software environment and evaluation methods for online learning from evolving streams. These are relevant architectural options and concepts, not technologies found in the repository. No Kafka, Spark, Hadoop, Flink, or MOA implementation should be claimed for this project unless later added and verified.

#### 2.4 Research Gaps

**Established challenges described in cited research:** short/noisy and context-dependent posts; domain/language variation; class imbalance; changing distributions/concept drift; data arriving out of order; benchmark variability and tool error; and operational trade-offs between freshness/latency, correctness/completeness, and cost. Public platform data also raises access, representativeness, privacy, and reproducibility concerns.

**Repository-demonstrated gaps:** the repository has no sentiment or streaming implementation, no defined data source, no labeled corpus, no classifier, no stream pipeline, no results dashboard, and no evaluation. This is a gap between the requested topic and the current codebase—not a research gap claimed as novel by the project.

**Potential project-level research questions (to confirm):** (a) how well does a chosen baseline classify sentiment in the selected domain? (b) what end-to-end processing latency and throughput does the prototype achieve under a stated load? (c) how do scores and errors vary by class, source, or time window? These questions are proposed; no answers/results exist in the repository.

### 3. Objectives of the Study

The following objectives are suitable for a proposed project and should be retained only if the scope is approved and implemented:

1. Review sentiment-analysis and stream-processing literature and define the post-level sentiment task and evaluation protocol.
2. Select a permitted, documented social-media data source or suitable labeled stream dataset and record its scope, collection method, and limitations.
3. Design and implement preprocessing and a justified baseline sentiment classifier; compare alternatives only if time/data permit.
4. Build a streaming or replay-based pipeline that processes records incrementally and produces timestamped sentiment aggregates.
5. Present aggregate outputs in a simple dashboard or report, with source/time-window context.
6. Evaluate predictive performance, class-wise errors, throughput, and latency on documented test conditions; report limitations and ethical/data-access constraints.

**Current completion status:** none of these sentiment-specific objectives is evidenced as implemented in the supplied repository.

### 4. Methodology

#### 4.1 Research Design

Use an applied design-science/software-prototype study with quantitative evaluation. Define the unit of analysis (one post), sentiment label set, language, target topic/domain, allowed data source, and time window before implementation. Compare a baseline against any proposed alternative on the same held-out labeled examples. Evaluate the integrated pipeline separately from model quality. This section is a recommended plan, not a description of completed experiments.

**Step 1 — Research design and problem identification (proposed):** define the research questions, inclusion criteria, scope, intended users, and hypotheses; map research constraints around source terms, privacy, and consent/ethics review. The repository supplies no such study protocol.

**Step 2 — Data acquisition (proposed):** choose either (a) a current platform’s officially permitted API and compliant access method, after checking the platform’s current terms/rate limits, or (b) a documented, appropriately licensed benchmark dataset for an offline/replay prototype. Record retrieval date, fields, language, sampling/filter rules, labeling provenance, and any exclusions. Do not imply live API access until credentials and an operational collector are verified. The repository has no social-media data acquisition.

**Step 3 — Preprocessing (proposed):** preserve an immutable raw record where permitted; normalize encoding; remove duplicates and malformed/empty records; handle URLs, mentions, hashtags, emoji, repeated characters, and language consistently; retain negation and sentiment-bearing tokens; tokenize according to the chosen model. Document each transformation and avoid cleaning that erases useful signals. No sentiment-text preprocessing exists in the code.

**Step 4 — Sentiment analysis/model selection (proposed):** establish a simple majority-class or lexicon baseline, then select a supervised model appropriate to available labels and compute constraints (for example, TF-IDF plus logistic regression/SVM for a classical baseline, or a transformer if justified). Use train/validation/test separation and avoid leakage across duplicated posts or time periods. Report label source, model version, hyperparameters, and class balance. No sentiment model or training/inference path is present.

**Step 5 — Stream processing and system integration (proposed):** specify record schema, timestamps, retry/error handling, deduplication, buffering, and windowing. If source access is unavailable, replay timestamped benchmark records at controlled rates and call the result stream replay rather than a live social feed. Select an actual broker/engine only after requirements justify it; none is present now. Integrate classifier output and aggregates with an API/storage layer only if those components are built.

**Step 6 — Result visualization (proposed):** show positive/negative/neutral counts or proportions by explicit event-time windows, volume, and data freshness; label the source and window. Avoid presenting sampled platform posts as representative population opinion. Current charts, if any, relate to resumes and do not visualize sentiment.

**Step 7 — Evaluation and testing (proposed):** report accuracy plus macro-F1, per-class precision/recall/F1, and confusion matrix; for imbalanced evolving streams consider a prequential/windowed measure such as the sliding-window Kappa discussed by Bifet and Frank. Measure end-to-end latency (state percentile definition), throughput, dropped/error rate, and resource use under repeatable load. Compare batch/offline model results with stream/replay results. Create unit/integration tests for preprocessing, prediction, aggregation, and API boundaries, then document dataset split, hardware, software versions, random seeds, and limitations. The repository contains no sentiment metrics, experiment results, or sentiment tests; no values are asserted here.

### References

1. Pang, B., & Lee, L. (2008). “Opinion Mining and Sentiment Analysis.” *Foundations and Trends in Information Retrieval*, 2(1–2), 1–135. DOI: [10.1561/1500000001](https://doi.org/10.1561/1500000001). **Key note:** broad survey of sentiment/opinion tasks, methods, resources, evaluation, and application considerations. **Relevance:** foundational definitions and task framing.
2. Medhat, W., Hassan, A., & Korashy, H. (2014). “Sentiment analysis algorithms and applications: A survey.” *Ain Shams Engineering Journal*, 5(4), 1093–1113. DOI: [10.1016/j.asej.2014.04.011](https://doi.org/10.1016/j.asej.2014.04.011). **Key note:** surveys algorithm families and applications. **Relevance:** supports comparing lexicon and machine-learning approaches.
3. Tsytsarau, M., & Palpanas, T. (2012). “Survey on mining subjective data on the web.” *Data Mining and Knowledge Discovery*, 24, 478–514. DOI: [10.1007/s10618-011-0238-6](https://doi.org/10.1007/s10618-011-0238-6). **Key note:** surveys sentiment/opinion mining, opinion aggregation, trends, and contradiction analysis over web content. **Relevance:** background for temporal aggregation and research context.
4. Cambria, E., Schuller, B., Xia, Y., & Havasi, C. (2013). “New avenues in opinion mining and sentiment analysis.” *IEEE Intelligent Systems*, 28(2), 15–21. DOI: [10.1109/MIS.2013.30](https://doi.org/10.1109/MIS.2013.30). **Key note:** discusses progression from heuristics and keyword methods toward discourse-aware, fine-grained, and concept-level analysis. **Relevance:** explains limitations of simplistic polarity scoring.
5. Sakaki, T., Okazaki, M., & Matsuo, Y. (2010). “Earthquake Shakes Twitter Users: Real-Time Event Detection by Social Sensors.” In *Proceedings of the 19th International Conference on World Wide Web (WWW ’10)*, 851–860. DOI: [10.1145/1772690.1772777](https://doi.org/10.1145/1772690.1772777). **Key note:** studies real-time event detection from Twitter using social sensors and spatiotemporal models. **Relevance:** prototype precedent for real-time social data; task is event detection rather than general sentiment classification.
6. Bifet, A., & Frank, E. (2010). “Sentiment knowledge discovery in Twitter streaming data.” In *Proceedings of the 13th International Conference on Discovery Science*, Lecture Notes in Computer Science 6332, 1–15. DOI: [10.1007/978-3-642-16184-1_1](https://doi.org/10.1007/978-3-642-16184-1_1). **Key note:** applies stream learning to Twitter sentiment and proposes sliding-window Kappa for imbalanced, time-changing streams. **Relevance:** directly informs streaming sentiment evaluation.
7. Abbasi, A., Hassan, A., & Dhar, M. (2014). “Benchmarking Twitter Sentiment Analysis Tools.” In *Proceedings of LREC 2014*, 823–829. Official record: [ACL Anthology L14-1406](https://aclanthology.org/L14-1406/). **Key note:** compares 20 tools on five test beds and includes error analysis. **Relevance:** supports systematic benchmark-based evaluation.
8. Akidau, T., Bradshaw, R., Chambers, C., Chernyak, S., Fernández-Moctezuma, R. J., Lax, R., McVeety, S., Mills, D., Perry, F., Schmidt, E., & Whittle, S. (2015). “The Dataflow Model: A Practical Approach to Balancing Correctness, Latency, and Cost in Massive-Scale, Unbounded, Out-of-Order Data Processing.” *Proceedings of the VLDB Endowment*, 8(12), 1792–1803. Official record: [Google Research publication](https://research.google/pubs/the-dataflow-model-a-practical-approach-to-balancing-correctness-latency-and-cost-in-massive-scale-unbounded-out-of-order-data-processing/). **Key note:** formalizes trade-offs and abstractions for unbounded/out-of-order data, including event-time/windowing concerns. **Relevance:** guides architecture and latency/correctness framing.
9. Carbone, P., Katsifodimos, A., Ewen, S., Markl, V., Haridi, S., & Tzoumas, K. (2015). “Apache Flink™: Stream and Batch Processing in a Single Engine.” *IEEE Data Engineering Bulletin*, 38(4), 28–38. Official author/open copy: [PDF](https://www.cs.usfca.edu/~mmalensek/cs677/schedule/papers/carbone2015flink.pdf). **Key note:** describes an engine that unifies stream and batch processing through pipelined fault-tolerant dataflows. **Relevance:** an architectural option to evaluate, not an existing project dependency.
10. Bifet, A., Holmes, G., Kirkby, R., & Pfahringer, B. (2010). “MOA: Massive Online Analysis.” *Journal of Machine Learning Research*, 11(52), 1601–1604. Official record: [JMLR](https://jmlr.org/papers/v11/bifet10a.html). **Key note:** introduces an environment for online learning from evolving streams and associated evaluation tools. **Relevance:** stream-learning/evaluation background; MOA is not used in this repository.

#### References in simple words

Ye papers aapke current code se features prove karne ke liye nahi hain. Ye topic samajhne, literature review likhne, aur future system ka design choose karne ke liye academic sources hain.

1. **Pang & Lee (2008):** Sentiment analysis kya hota hai aur researchers ise kaise karte hain—iska bada introductory overview. Isse Introduction aur basic definitions samajhne mein madad milegi.
2. **Medhat et al. (2014):** Sentiment nikalne ke alag tareeqe—jaise word-list/lexicon aur machine-learning models—ka survey. Isse Methodology mein model options explain kar sakte ho.
3. **Tsytsarau & Palpanas (2012):** Websites aur online posts se opinions ko collect, summarize, aur time ke saath compare karne par overview. Isse social-media opinions aur trends ka background milega.
4. **Cambria et al. (2013):** Sirf positive/negative words ginne se kaam kyun nahi chalta—context, sentence structure, aur meaning bhi matter karte hain. Isse simple sentiment methods ki limitations samajh aati hain.
5. **Sakaki et al. (2010):** Twitter messages se earthquake jaise event ko jaldi detect karne ka example. Ye real-time social-media processing ka example hai, lekin paper ka main task sentiment classification nahi hai.
6. **Bifet & Frank (2010):** Lagataar aate Twitter data par sentiment classification aur usko changing stream mein evaluate karne ka research. Aapke topic ke sabse seedhe references mein se ek hai.
7. **Abbasi et al. (2014):** Researchers ne kai Twitter sentiment tools ko alag test datasets par compare kiya aur unki galtiyon ka analysis kiya. Iska lesson: model ko test kiye bina uski accuracy assume nahi karni chahiye.
8. **Akidau et al. (2015):** Continuous data pipelines mein result kitna jaldi chahiye, kitna sahi chahiye, aur kitna kharcha hoga—inke beech trade-off samjhata hai. Ye system architecture ka conceptual reference hai.
9. **Carbone et al. (2015):** Apache Flink naam ke stream/batch processing engine ko explain karta hai. Ye ek possible technology ka academic reference hai; aapke current repository mein Flink nahi hai.
10. **Bifet et al. (2010), MOA:** MOA ek research software environment hai jisme stream-learning algorithms ko run/evaluate kiya ja sakta hai. Ye stream evaluation samjhata hai; aapke current project mein MOA installed ya used nahi hai.

**Synopsis mein citation kaise use karein:** jis statement ka support chahiye, uske baad author-year citation lagao, jaise “online text sentiment analysis ko survey literature mein opinion mining bhi kaha gaya hai (Pang & Lee, 2008).” Reference list mein usi citation ka poora title, publication details, aur DOI/official link do. Paper ne jo kaam kiya hai bas wahi claim karo; use apne system ka feature ya apna experimental result mat batao.

## Missing information and questions for the project owner

1. Is the intended deliverable a new sentiment-analysis project to be built in this repository, or is this the wrong repository for the synopsis topic? The current codebase is demonstrably a resume-builder.
2. If this repository is intended as the starting point, should the project retain its React/Spring/MongoDB stack, or is a separate implementation expected? No NLP or stream stack is currently declared.
3. Which social platform/source is in scope, and do you have authorized access/API credentials or a particular approved dataset? Platform availability and terms must be checked at implementation time.
4. Which language, domain/topic, sentiment labels (binary, three-class, or fine-grained), and time range should define the study?
5. Is the expected system genuinely live, or is replay of a static dataset acceptable for the academic prototype?
6. What hardware, deployment environment, institutional ethics/privacy requirements, and expected synopsis citation style should be used?
7. Should the objectives and methodology remain prospective until new implementation exists, or is there an untracked/not-included companion prototype or report that documents completed work?

## Source files inspected

Repository inventory included `Frontend/resume-builder/README.md`, `package.json`, frontend app/routes, API path and Axios utilities, resume forms/templates/PDF utilities, `backend/resumebuilderapi/pom.xml`, Spring controllers/services, MongoDB document/repository classes, and the available backend help/config files. Searches for sentiment, social-media, stream, Kafka, Spark, Hadoop, and testing references found no topic implementation. Academic metadata and summaries were checked against publisher, university-repository, JMLR, ACL Anthology, and Google Research records linked above.
