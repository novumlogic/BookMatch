# BookMatch - A KMP Project

BookMatch is a book recommendation app that leverages generative AI to provide personalized book recommendations. It allows users to refine their preferences through genre selection, likes/dislikes, and ratings, and continuously improves recommendations based on this feedback. The app also features the ability to filter and access previously generated recommendations.

# Screenshots
### Android

<img src="screenshots/img1.png" width=300/> <img src="screenshots/img2.png" width=300/> <img src="screenshots/img3.png" width=300/> <img src="screenshots/img4.png" width=300/> <img src="screenshots/img5.png" width=300/>

### ios

<img src="screenshots/img6.png" width=300/> <img src="screenshots/img7.png" width=300/> <img src="screenshots/img8.png" width=300/> <img src="screenshots/img9.png" width=300/> <img src="screenshots/img10.png" width=300/> 

# Purpose
The purpose of BookMatch is to offer users an intuitive and dynamic way to discover new books tailored to their tastes. By combining user input with the power of generative AI, BookMatch aims to provide highly relevant and enjoyable book suggestions.

# Workflow
1. Genre Selection:
- Users start by opting out of genres they are not interested in.
- Initial recommendations are generated based on the selected genres.

2. Recommendations:
- Users receives a list of book recommendations.
- Users can like, dislike, and rate books.
- Users can change the genres selected anytime.
- Based on user feedback (likes, dislikes, ratings), new recommendations are generated using the LLM.

3. Filtering Recommendations:
- Users can filter and access old recommendations.
- Recommendations are stored and can be revisited at any time.

# Tech Stack 
- **Kotlin Multiplatform (KMP)**:
   - Shared business logic is written in Kotlin.
   - Native UI for Android using Jetpack Compose.
   - Native UI for iOS using SwiftUI.

- **Backend**:
   - Supabase is used for backend services.

- **Generative AI**:
   - OpenAI is used for generating book information.

- **Authentication**:
   - Google OAuth is used for authentication in both the Android and iOS apps via Supabase.
 
# Project Structure
### Shared Module 
- Contains business and shared logic for both apps i.e. composeApp and iosApp

|Directory | Description | Important 
| --- | --- | ---
|**api** | Contains API client files for accessing external service like OpenAi, Gemini | *‘GeminiClient.kt’*, *‘OpenAiClient.kt’*
**data** | Contains file for database operations and authentication operations | _‘RemoteDataSource.kt’_, _‘SupabaseProvider.kt’_
**model** | Defines model file to parse the request and response from AI as well as database tables | _‘OpenAiRequest.kt’, ‘OpenAiResponse.kt’, ‘SupabaseRemoteEntities.kt’_
**utils** | Contains Constants classes which store api url and keys and extension functions | _‘Constants.kt’_

- **Note:**

  - The [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt) file includes the following properties which you can update with their own values: 
      - ```OAUTH_WEB_CLIENT_ID```: Google web client client id generated from google cloud console to use sign in with           google feature
      - ```OPEN_AI_API_KEY```: Your OpenAI api key
      - ```GEMINI_API_KEY```: Your Gemini api key (if you want to use gemini instead of OpenAi along with GeminiClient)
  - The [GeminiClient.kt](../main/shared/src/commonMain/kotlin/api/GeminiClient.kt) file is included for those who want to use Gemini for book recommendation generation. Replace ```GeminiClient```'s functions with ```OpenAiClient``` in ```MainViewModel.kt``` of composeApp and SharedViewModel.swift of iosApp
  - You can edit ```systemInstruction``` property inside [OpenAiClient](../main/shared/src/commonMain/kotlin/api/OpenAIClient.kt)/[GeminClient](../main/shared/src/commonMain/kotlin/api/GeminiClient.kt)  to customize the AI response according to your own needs.
 
# Backend Structure
### Database tables 

1. **Users**
- Used to store user’s info and also maintains certain info from their login session.

| Fields                    | Datatype   | Constraint                                      | Description                                                     |
|---------------------------|------------|-------------------------------------------------|-----------------------------------------------------------------|
| user_id                   | UUID       | primary-key, default = auth.uid()               | unique identifier for user                                      |
| email                     | text       | not null, unique                                | user's email provided via Google OAuth                         |
| display_name              | text       | not null                                        | username provided via Google OAuth                             |
| avatar_url                | text       |                                                 | user's profile photo provided via Google OAuth                 |
| category_shown            | boolean    |                                                 | Describes if the user was shown category selection in the login session |
| selected_categories       | text       |                                                 | Describes the selected categories/genres by the user           |
| last_recommendation_time  | int8       |                                                 | Describes the last recommendation generation timestamp         |
| created_at                | timestampz | not null                                        | Stores the first time the account was logged in the app        |



2. **Categories** (Genres)
- To store the genres/categories of books from which user can generate his recommendation

| Fields          | Datatype | Constraint  | Description                            |
|-----------------|----------|-------------|----------------------------------------|
| category_id     | int2     | primary-key | uniquely identify each genre/category  |
| category_name   | text     | not-null    | name of each genre                     |
| category_emoji  | text     |             | emoji for each genre                   |

3. **Books**
- To store generated book’s information

| Fields                  | Datatype | Constraint                                                                                      | Description                                                                 |
|-------------------------|----------|-------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| book_id                 | int8     | primary-key                                                                                    | unique identifier                                                           |
| book_name               | text     | not null, unique                                                                               | book title                                                                  |
| author_name             | text     | not null, unique                                                                               | author name                                                                 |
| genre_tags              | text[]   |                                                                                                 | applicable genre tags                                                       |
| category_id             | int2     | not null, foreign key referring category_id of categories table with on update cascade, on delete cascade | refers to the category in the categories table                              |
| description             | text     | not null                                                                                        | 2 line description                                                          |
| pages                   | text     | not null                                                                                        | number of pages in the book                                                 |
| isbn                    | text     | not null                                                                                        | unique ISBN number to fetch book cover using Open Library Book Cover API    |
| first_date_of_publication | text   | not null                                                                                        | first date of publication                                                   |
| reference_link          | text     | not null                                                                                        | URL to open book-related webpage                                            |


4. **Recommendations**
- To store the recommendation generated by each user

| Fields           | Datatype | Constraint                                                                                               | Description                                                     |
|------------------|----------|----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| recommendation_id | int8     | primary-key                                                                                              | unique identifier for recommendations                           |
| timestamp        | int8     | not null                                                                                                 | stores UNIX epoch time                                          |
| user_id          | UUID     | not null, foreign key referring to users table user_id field with on update cascade, on delete cascade, default = auth.uid() | uniquely identifies which recommendation belongs to which user  |

5. **Recommended_books**
- To store the books generated in each recommendation seperately


| Fields               | Datatype | Constraint                                                                                      | Description                                                     |
|----------------------|----------|-------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| id                   | int8     | primary-key                                                                                    | uniquely identifies each book for a given recommendation_id     |
| recommendation_id    | int8     | not null, foreign key referring to users table user_id with on update cascade, on delete cascade | to access corresponding info in the recommendation table        |
| book_id              | int8     | foreign key referring to books table book_id field with on update cascade, on delete cascade     | to access the book’s info from the books table                  |
| liked                | boolean  |                                                                                                 | indicates if the book is liked by the user                      |
| rating               | int2     | check value is between 0 and 6                                                                  | rating between 1 to 5 given by the user                         |
| last_updated_time    | int8     | default = 0                                                                                     | last time the user performed any interaction                    |
| read                 | boolean  | default = false                                                                                 | indicates if the user has read the book                         |

6. **Chat_history**
- To store the chat_history which is used to provide context when sending chat_completion request to LLM
  
| Fields      | Datatype | Constraint                                             | Description                                                     |
|-------------|----------|--------------------------------------------------------|-----------------------------------------------------------------|
| id          | int8     | primary-key                                            | unique identifier                                               |
| timestamp   | int8     | not null                                               | stores UNIX epoch time                                          |
| user_id     | UUID     | foreign key referring to users table user_id field     | identifies to which user the chat history belongs               |
| user_text   | text     |                                                        | user message sent to OpenAI or any other LLM’s API request      |
| ai_answer   | text     |                                                        | response received from AI                                       |


# Getting Started

## Prerequisites
- [Kotlin Multiplatform Mobile](https://www.jetbrains.com/help/fleet/getting-started-with-kotlin-multiplatform.html)
- [Jetpack Compose](https://developer.android.com/compose)
- [SwiftUI](https://developer.apple.com/xcode/swiftui/)
- [Supabase](https://supabase.com)
- [OpenAI chat completions API](https://platform.openai.com/docs/guides/chat-completions)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Dhananjay-Navlani/bookmatch.git
cd BookMatch
```


2. Set up the backend:
  - Configure Supabase with the necessary tables and permissions and add your ```SUPABASE_URL``` and ```SUPABASE_API_KEY``` inside [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt).
  - Set up the ```OPENAI_API_KEY``` inside [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt)  for book information generation.
  - Enable Google OAuth in Supabase for user authentication and also Web client id from google cloud console inside Client ID (for oauth) in Supabase authentication and under Constants.kt file’s OAUTH_WEB_CLIENT_ID property in the shared module.

3. Build the project:

- For Android:
```bash
./gradlew build 
```
- For iOS: 
  - Run the following command ```./gradlew :shared:assembleXCFramework``` to convert the shared module into framework which needs to be included in iosApp.
  - Open iosApp.xcodeProj under iosApp module in Xcode

4. Run the project:
- For Android:
```
./gradlew installDebug
```

- For iOS: Run the project from Xcode on a simulator or device.

 

###  Contributing
We welcome contributions from the community. Please fork the repository and create a pull request with your changes.

*** 

Feel free to reach out if you have any questions or need further assistance. Enjoy discovering your next favorite book with BookMatch

