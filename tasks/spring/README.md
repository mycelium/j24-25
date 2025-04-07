## Spring Framework task

### Common requirements (for all variants):

#### 1. Sources and Artifacts
- **Version Control and Collaboration**
  - All code must be committed to a GitHub repository (one repository per team).
  - The repository must include a README.md file with a detailed guide on how to:
    - Set up the project locally. 
    - Build and run the application. 
    - Deploy the application using Docker. 
    - Interact with the Telegram bot.
  - Commit history should reflect meaningful contributions from all team members.
- **Target Artifacts**
  - The application must produce a runnable fat JAR as the primary artifact. 
  - The application must be deployable as a Docker container.
  - Use a build tool (Gradle, Maven or SBT) to manage dependencies, build, and package the application.
#### 2. Technology Stack
- **Core Technologies**
  - Telegram API: The application must integrate with the Telegram API to provide bot functionality. 
  - Programming Language (use one of the following):
    - Java 23 
    - Kotlin 
    - Scala 3 
  - Framework: 
    - Use Spring 6 for dependency injection and application structure. 
    - **Spring Boot is strictly prohibited for all students.**
  - Message queue
    - Use message queue specified in google doc. **IF NEEDED**
- **Deployment and Containerization**
  - Use Docker to containerize the application. 
  - Provide a Docker Compose file for local development and testing. 
  - Publish the Docker image to Docker Hub for deployment.

#### 3. Code Quality and Best Practices
- **Code Style**
  - Adhere to a recognized Java code style guide (e.g., Google Java Style Guide, Oracle Code Conventions). 
  - Bonus: Use static code analysis tools (e.g., Checkstyle, PMD, or SonarQube) to enforce code quality.
- **Modern Language Features**
  - Use modern language constructs and features available in the chosen language. 
  - Code written in outdated styles (e.g., Java 1.5 style) will not be accepted.
- **Clean Code Principles**
  - Follow clean code principles: meaningful variable names, proper encapsulation, and modular design. 
  - Avoid code duplication and ensure proper error handling.

#### 4. Submission Guidelines
- **To submit the coursework, provide the following:**
  - Requirements
  - Architecture design
  - GitHub Repository Link:
    - Include a link to the GitHub repository with the complete codebase. 
    - Ensure the repository has a clear and detailed README.md file. 
  - Docker Hub Link:
    - Provide a link to the Docker Hub repository where the Docker image is published. 
  - Telegram Bot Link:
    - Share the link or username of the Telegram bot for testing and interaction.
- **Submission process**
  - **Step 1: Requirements**
    - Provide a detailed requirements document that outlines:
      - The purpose and functionality of the application. 
      - Key features of the Telegram bot. 
      - Non-functional requirements (e.g., performance, scalability, etc.). 
      - Any assumptions or constraints. 
    - This document should be clear, concise, and well-structured.
  - **Step 2: Architecture**
    - Submit an architecture document that includes:
      - A high-level overview of the system design.
      - Diagrams (e.g., component diagrams, sequence diagrams, or flowcharts) to illustrate the architecture.
      - Explanation of the chosen technology stack and its justification.
      - Description of how the application will be built, deployed, and run.
    - The architecture document should demonstrate a clear understanding of the system's structure and design decisions.
  - **Step 3: Full Project**
#### 5. Additional Notes
- **Teamwork:** All team members must contribute equally. The commit history should reflect individual contributions. 
- **Documentation:** Proper documentation (in-code comments, README, etc.) is mandatory. 
- **Testing:** While not explicitly required, writing unit tests or integration tests is highly encouraged. 
- **Deadline:** Ensure the coursework is submitted before the deadline. Late submissions will not be accepted.
- **Specific requirements** (technical restrictions, etc): [google sheet](https://docs.google.com/spreadsheets/d/1ly-yUu19S_mU7tSG_QLFaJOkQIM-uzzmXwLm4BGYW8Q)
#### 6. Functional requirements
- **Common**
  - Authorization
    - via telegram
    - via http
  - Roles
    - `User`
    - `Admin`
  - Healthcheck endpoint (no authorization required)
    - `/healthcheck`
      - server status
      - list of authors (students) 
  - Users endpoint (only for admins)
    - List of all users
- **Variant specific**
  - Must be done by yourself

### Variants

#### 1. Weather Bot

**1.1. Location Management**
- Set location (city or geomarker).
- Show current location.
- Update location.

**1.2. Weather Information**
- Show current weather (now + rest of the day).
- Show weather forecast:
  - Today, tomorrow, and next 3 days (morning, noon, evening). 
  - Show weather for another city. 

**1.3.Scheduling**
- Schedule weather updates (e.g., every morning).

**1.4. Bonus Features**
- Alarm users about upcoming:
  - Precipitation (rain, snow, etc.). 
  - Cataclysms (storms, hurricanes, etc.).
- Accept live user location and use it for weather updates and alarms.

**1.5.Notes**
- Use any open weather API (e.g., OpenWeatherMap).

#### 2. Quiz Bot

**2.1. Questions & Answers**
- Add questions with answers and tags.

**2.2. Quiz Functionality**
- Automatically send one random question by schedule.
- Ask for a random question.
- Ask for a random question from a specific theme (by tags).

**2.3. Score Management**
- Show user score.
- Reset score (drop to zero).
- Show score by themes (tags).

**2.4. Bonus Features**
- Groups:
  - Create groups.
  - Invite members.
  - Group score table. 
  - Group question by schedule (random question from all group users). 
- Neighbors:
  - Questions from/to neighbors (by location).
  - Share score with neighbors (show distance).

#### 3. Task Management Bot

**3.1. Task Management**
- Create tasks (summary, estimation, deadline).
- Update tasks (add spent time, mark as done, change deadline).
- Show tasks by deadline (today, tomorrow, week).

**3.2. Recurring Tasks**
- Create recurring tasks (hourly, daily, weekly, monthly).
- Show, update, and delete recurring tasks.

**3.3. Reminders**
- Remind about deadlines.
- Remind about not updated tasks.

**3.4. Bonus Features**
- Integration with issue trackers (YouTrack, ClickUp, Jira, Trello, etc.).
- Integration with calendar.

#### 4. Movie Recommendation Bot

**4.1. Preferences**
- Show user preferences.
- Add preferences (e.g., genre, actors, release year).
- Delete preferences.

**4.2. Film Search**
- Request a film with query (genre, actors, release year, keywords, etc.).
- Use user preferences if criteria are not set.
- Request a random film (skip criteria and preferences)

**4.3. Watchlist**
- Add films to watchlist.
- Show watchlist.
- Mark films as watched.
- Remove films from watchlist.

**4.4. Bonus Features**
- Add ratings to watched films (-10 to 10).
- Find similar films.
- Show all watched films for a period.
- Share watchlist with other users.
- Share watched films for a period with other users.

#### 5. Currency Converter Bot

**5.1. Preferences**
- Set home currency (e.g., RUB).
- Set default currency pair (e.g., RUB-USD).

**5.2. Converter**
- Request exchange rate:
  - Default pair if nothing is specified.
  - Compare with home currency if one currency is specified.
  - Show exchange rate if two currencies are specified.
- Convert specified amounts using the same rules.

**5.3. History**
- Show all requests for a specified period.
- Show requests for a specific currency/pair for a period.

**5.5. Alarms**
- Alarms:
  - For desired exchange rates
  - If exchange rate changed significantly 

**5.6. Bonus Features**
- Add math operations (+, -, *, /, brackets) with currency conversion.
  - Allow calculations with different currencies (e.g., 24USD + 15.5EUR + 17RUB).

#### 6. Event Reminder Bot

**6.1. Event Management**
- Create events (summary, date + time, duration).
- List events (next, day, week).
- Import events as .ics files.
- Delete and update events.

**6.2. Recurring Events**
- Create recurring events (hourly, daily, weekly, monthly).
- Show, update, and delete recurring events.

**6.3. Reminders**
- Remind about upcoming events.

**6.4. Bonus Features**

- Integration with calendars (Google, Yandex, Teamup, etc.).
  - Add events to calendars from the bot.
  - Remind about upcoming events from calendars.
- Collaborative events: Add collaborators to events. 
  - Show nearest events by geolocation.

#### 7. Restaurant Finder Bot

**7.1. Preferences**
- Show user preferences.
- Add preferences (e.g., vegetarian, allergies, Italian, fish, etc.).
- Delete preferences.

**7.2. Restaurant Search**
- Request a restaurant with query (location, cuisine, keywords, etc.).
- Use user preferences if criteria are not set.
- Allow skipping criteria.
- Request a random restaurant (location + area).

**7.3. Visit List**
- Add restaurants to visit list.
- Show visit list.
- Mark restaurants as visited.
- Remove restaurants from visit list.

**7.4. Bonus Features**
- Add ratings to restaurants (-10 to 10).
- Find similar restaurants.
- Add ratings to dishes.
- Create routes for visiting multiple restaurants to try different dishes.
  - Save and share routes.

#### 8. Trip Planner Bot

**8.1. Planned Trips**
- Show all planned trips.
- Plan a trip (create trip, set points, and routes with dates).
- Delete planned trips. 

**8.2. Trip Helper**
- Remind about closest trips.
- When a trip starts:
  - Send location to mark points as visited.
  - Allow manual marking of points. 
- Add notes to points.

**8.3. Trip History**
- List all finished trips.
- Show details for specific finished trips.
- Add scores for finished trips.

**8.4. Bonus Features**
- Photos
  - Add photos to points.
  - Show photos for finished trips.
  - Create collages from photos by points.
- Suggest trips for users:
  - Based on visited points.
  - Using LLM for new recommendations.

#### 9. CryptoCurrency Bot

**9.1. Preferences**
- Set default cryptocurrency (e.g., BTC, ETH).
- Set default fiat currency (e.g., USD, EUR).

**9.2. Crypto Information**
- Show current price of a cryptocurrency.
- Show price history for a specified period (e.g., last 7 days, 30 days).
- Compare prices of multiple cryptocurrencies.

**9.3. Portfolio Management**
- Add cryptocurrencies to a portfolio.
- Track portfolio value over time.
- Show profit/loss for each cryptocurrency in the portfolio.

**9.4. Alerts**
- Set price alerts for specific cryptocurrencies (e.g., notify when BTC > $50,000).
- Automatic alerts when significant growth happening
- Show active alerts.
- Delete alerts.

**9.5. Bonus Features**
- Trend Analysis:
  - Show trending cryptocurrencies (e.g., top gainers/losers in the last 24 hours).
- News Integration:
  - Fetch and display the latest cryptocurrency news.
- Advanced Analytics:
  - Show market cap, trading volume, and other key metrics.

#### 10. Anime Bot

**10.1. Preferences**
- Set favorite genres (e.g., action, romance, fantasy, shonen...).
- Set favorite studios (e.g., Studio Ghibli, MAPPA).
- Set preferred release years.
- Set preferred voice actor

**10.2. Anime Search**
- Search for anime by title, genre, studio, or release year.
- Show detailed information about an anime (e.g., synopsis, episodes, rating).
- Request a random anime based on preferences.

**10.3. Watchlist**
- Add anime to watchlist.
- Show watchlist.
- Mark anime as watched.
- Remove anime from watchlist.

**10.4. Recommendations**
- Get personalized anime recommendations based on preferences and watch history.
- Find similar anime to a specific title.
- Provide links to streaming platforms (e.g., Crunchyroll, Funimation).
- Show upcoming seasonal anime.
  - Notify users about new seasonal releases.

**10.5. Episode Tracking**
- Track watched episodes for ongoing anime.
- Get reminders for new episodes of ongoing anime.

**10.6. Bonus Features**
- Ratings and Reviews:
  - Add ratings and reviews to watched anime. 
  - Show average ratings and reviews for an anime.
- Community Features:
  - Create groups to discuss anime. 
  - Share watchlists with friends.
- Manga Integration:
  - Search for manga related to an anime.
  - Track manga reading progress.
