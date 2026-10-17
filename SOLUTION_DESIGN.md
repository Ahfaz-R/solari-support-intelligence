# Solution Design — Solari Support Intelligence

## 1. The Problem

When a customer reports a problem, support engineers often have to investigate it from scratch. Sometimes, another customer has already reported the same issue and the team has fixed it before. Finding that old ticket and understanding how it was solved can take time.

Solari Support Intelligence helps support engineers find similar past tickets and learn from their solutions. It can also use AI to suggest what to try next.

The goal is to help engineers solve problems faster, not to replace their judgment. Engineers should always check that a suggested solution is suitable before applying it.

## 2. Assumptions

While building this application, I made a few assumptions:

- **Past tickets contain useful information:** Historical tickets should include a description of the problem and, ideally, how it was fixed.
- **New tickets may arrive in batches:** A user should be able to submit several new tickets together instead of entering them one by one.
- **Not every problem has been seen before:** If the application cannot find useful past examples, it should say so rather than make up an answer.
- **AI should be optional:** The application should still work when an AI service is unavailable or an API key is not configured.
- **People make the final decision:** AI suggestions are helpful starting points, not guaranteed solutions.

## 3. How the Application Works

The application has three main parts:

- **Angular:** The screen where users enter or upload new tickets and view the results.
- **Spring Boot:** The part that receives tickets, searches for similar cases, and prepares recommendations.
- **PostgreSQL:** The database that stores historical tickets and their recorded solutions.

The process works like this:

```text
New support ticket
        |
        v
Search past tickets
        |
        v
Show the most similar cases
        |
        v
AI reviews the ticket and past solutions
        |
        v
Suggest a solution, or explain
why there is not enough evidence
        |
        v
Support engineer reviews the result
```

When a new ticket arrives, the application searches the database using words from its subject and description. It ranks the matching tickets and returns a small number of the most relevant results.

AI then reviews the new ticket alongside those past cases. It can suggest a solution, explain why it might apply, and list useful next steps. It also identifies the historical tickets that support its suggestion.

If the past tickets do not provide enough evidence, the application can report that no reliable recommendation is available. If the AI service fails, a simpler rule-based method can provide a recorded solution from the best-ranked historical ticket. That fallback is deliberately marked as low confidence.

New tickets are analyzed but are not currently saved as historical tickets.

## 4. Important Choices and Trade-offs

**Using the existing database for searching**

The application uses PostgreSQL to find tickets containing relevant words. This keeps the design simple because the project already uses PostgreSQL, and it avoids introducing another system just for searching.

However, tickets that describe the same problem using very different words may not always match well. The current sample contains only 40 historical tickets, so more testing is needed to understand how well the search works with a much larger collection.

**Using AI with past solutions**

AI can help compare the new problem with past examples and explain which solutions may be useful. However, it can misunderstand a problem or suggest something that does not apply.

To reduce this risk, the application sends AI only the relevant tickets found by the search. It also checks that supporting ticket IDs refer to tickets that were actually retrieved. The results still need to be reviewed by a support engineer.

**Keeping a fallback option**

AI services can be unavailable or require a paid API key. A rule-based fallback allows the application to continue working without depending entirely on AI. The fallback is simpler, though, and may choose a past solution that is not truly relevant.

**Limiting the number of tickets processed together**

The application currently accepts up to 50 new tickets in one request and requests up to five historical matches per ticket. This is suitable for the initial demonstration, but larger workloads will need performance testing.

## 5. What I Would Improve Next

With more time, I would test the application against a larger collection of tickets and check whether the most useful past cases appear near the top of the results.

I would also improve how the application identifies tickets that describe the same problem using different words. AI-powered search could help, but I would add it only after testing showed that the current approach was not good enough.

I would add more automated tests for missing information, weak matches, AI failures, and incorrect responses. I would also improve error handling and avoid keeping database operations open while waiting for a response from the AI service.

With more data and budget, I would consider stronger search tools, monitoring, access controls, and a way to record which recommendations engineers accepted or rejected.

The current application is a working proof of concept. It demonstrates how historical tickets and AI can help support engineers investigate problems more efficiently. It still needs more testing and additional security and reliability work before it could be used in a production environment.