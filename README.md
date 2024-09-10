# BookMatch - A KMP Project

BookMatch is a book recommendation app that leverages generative AI to provide personalized book recommendations. It allows users to refine their preferences through genre selection, likes/dislikes, and ratings, and continuously improves recommendations based on this feedback. The app also features the ability to filter and access previously generated recommendations.


# Screenshots
### Android

<img src="screenshots/img1.png" width=300/> <img src="screenshots/img2.png" width=300/> <img src="screenshots/img3.png" width=300/> <img src="screenshots/img4.png" width=300/> <img src="screenshots/img5.png" width=300/>

### iOS

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

- **Database**:
    - Supabase is used for backend services like auth & database.

- **Generative AI**:
    - OpenAI is used for generating book information.

- **Authentication**:
    - Google OAuth is used for authentication in both the Android and iOS apps via Supabase.

- **Backend**:
    - Ktor framework is used to write server side code as well as for calling http methods at client side.


# Two Approaches to use BookMatch
To provide flexibility, BookMatch has been developed with two approaches for interacting with OpenAI APIs. You can choose the method that best suits your requirements—either through a Ktor backend for enhanced security or by directly calling the OpenAI API from the client side code for simplicity.

1. Directly Calling OpenAI API from the client code:
- Branch: [main](https://github.com/novumlogic/BookMatch/tree/main)
- For those who prefer a simpler, more straightforward setup, the main branch of BookMatch allows the mobile app to directly communicate with the OpenAI API. This method requires you to manage your API keys within the app itself. While this approach may be more convenient for quick prototypes or smaller projects, it comes with some security risks as sensitive data is stored client-side.


2. Using a Ktor Rest API:
- Branch: [bookmatch_backend](https://github.com/novumlogic/BookMatch/tree/bookmatch_backend)
- This approach introduces a Ktor backend that acts as a secure gateway between the app and OpenAI. In the bookmatch_backend branch, ktor apis are called, ensuring that sensitive data like OpenAI API keys are kept secure and not exposed on the client side. The backend also adds additional layers of protection, such as Supabase authentication and rate limiting. This is ideal for developers who prioritize security
- Visit the [BookMatch-Ktor-Backend](https://github.com/novumlogic/BookMatch-Ktor-Backend) repository or check out the blog post for an in-depth guide of setting up backend with Ktor .



# Project Structure
### [Shared Module](../main/shared/src/commonMain/kotlin)
<img src="https://github.com/user-attachments/assets/ef4e9431-87de-4ed2-b4ce-16b96a02edaf" width=500/>

- Contains business and shared logic for both apps i.e. composeApp and iosApp

|Directory | Description | Important file
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
    - You can edit ```systemInstruction``` property inside [OpenAiClient](../main/shared/src/commonMain/kotlin/api/OpenAIClient.kt)/[GeminiClient](../main/shared/src/commonMain/kotlin/api/GeminiClient.kt)  to customize the AI response according to your own needs.

### [Android UI](../main/composeApp/src/androidMain/kotlin/com/novumlogic/bookmatch)
<img src="https://github.com/user-attachments/assets/33e282b3-4f06-4cf3-96f5-ecfebce61b2d"/>

- The Android UI specific code is written under composeApp module's androidMain with jetpack compose. It contains code for all screens along with viewmodel which connect the data layer i.e. shared module to UI layer

### [ios UI](../main/iosApp/iosApp)
<img src="https://github.com/user-attachments/assets/950e3231-b41a-4c49-af58-ea99882dd8d0" /> 

- The ios UI specific code is written under iosApp with swiftui along with viewmodel which connects data layer i.e. shared module to UI layer


# Database Schema
### Database tables

1. **Users**
- Used to store user’s info and also maintains certain info from their login session.

| Fields                    | Datatype   | Constraint                                      | Description                                                     |
|---------------------------|------------|-------------------------------------------------|-----------------------------------------------------------------|
| user_id                   | UUID       | primary-key, default = auth.uid()               | unique identifier for user                                      |
| email                     | text       | not null, unique                                | user's email provided via Google OAuth                         |
| display_name              | text       | not null                                        | username provided via Google OAuth                             |
| avatar_url                | text       |                                                 | user's profile photo provided via Google OAuth                 |
| category_shown            | boolean    |                                                 | Describes if the user was shown category selection screen in the login session |
| selected_categories       | text       |                                                 | Describes the selected categories/genres by the user           |
| last_recommendation_time  | int8       |                                                 | Describes the last recommendation generation timestamp of user |
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


- Overview
    - A user can have many recommendations (one to many)
    - A recommendation can have many recommended_books (one to many)
    - A recommended_book has one to one relation with book (one to one)
    - A user can have many chat_history data (one to many)

# Getting Started

## Prerequisites
- [Kotlin Multiplatform Mobile](https://www.jetbrains.com/help/fleet/getting-started-with-kotlin-multiplatform.html)
- [Jetpack Compose](https://developer.android.com/compose)
- [SwiftUI](https://developer.apple.com/xcode/swiftui/)
- [Supabase](https://supabase.com)
- [OpenAI chat completions API](https://platform.openai.com/docs/guides/chat-completions)
- [Ktor](https://ktor.io/docs/welcome.html)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/novumlogic/BookMatch.git
cd BookMatch
```

2. Set up the database:
- Configure Supabase with the necessary tables as explained in [Database Structure Section](#database-schema) above and permissions and add your ```SUPABASE_URL``` and ```SUPABASE_API_KEY``` inside [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt).
- Also deploy the following RPC functions in your project in order to make the project work:
    - **Navigate to SQL Editor**:
        - On the left-hand menu, click on `SQL Editor`.

    - **Create a New SQL File**:
        - Click on the `New Query` button to create a new SQL file.

    - **Add the RPC Function Code**:
        - Copy and paste the following 3 RPC functions code into the SQL editor:
    ```
       CREATE OR REPLACE FUNCTION bulk_insert_books (books JSONB) RETURNS JSONB LANGUAGE plpgsql AS $$
       DECLARE
           inserted_books JSONB := '[]'::jsonb;
           duplicate_books JSONB := '[]'::jsonb;
           book_record JSONB;
           b_name TEXT;
           a_name TEXT;
           genre_tags TEXT[];
           category_id INT;
           description TEXT;
           pages INT;
           isbn TEXT;
           first_date_of_publication TEXT;
           reference_link TEXT;
           inserted_book JSONB;
           current_max_book_id INT;
           new_book_id INT;
       BEGIN
           -- Get the current maximum book_id
           SELECT COALESCE(MAX(book_id), 0) INTO current_max_book_id FROM books;
       
           FOR book_record IN SELECT * FROM jsonb_array_elements(books)
           LOOP
               -- Extract fields from the JSONB record
               b_name := book_record ->> 'book_name';
               a_name := book_record ->> 'author_name';
       
               -- Increment the book_id for the new book
               new_book_id := current_max_book_id + 1;
       
               BEGIN
                   -- Insert data with conflict handling
                   WITH ins AS (
                       INSERT INTO books (
                           book_id,
                           book_name,
                           author_name,
                           genre_tags,
                           category_id,
                           description,
                           pages,
                           isbn,
                           first_date_of_publication,
                           reference_link
                       )
                       VALUES (
                           new_book_id,
                           b_name,
                           a_name,
                            ARRAY(SELECT jsonb_array_elements_text(book_record -> 'genre_tags')),
                           (book_record ->> 'category_id')::INT,
                           book_record ->> 'description',
                           (book_record ->> 'pages')::INT,
                           book_record ->> 'isbn',
                           (book_record ->> 'first_date_of_publication')::TEXT,
                           book_record ->> 'reference_link'
       
                       )
                       ON CONFLICT (book_name, author_name) DO NOTHING
                       RETURNING book_id, book_name
                   )
                   SELECT jsonb_build_object('book_id', book_id, 'book_name', book_name)
                   INTO inserted_book
                   FROM ins;
       
                   -- Check if the insert was successful
                   IF inserted_book IS NOT NULL THEN
                       inserted_books := inserted_books || jsonb_build_array(inserted_book);
                       -- Update the current maximum book_id
                       current_max_book_id := new_book_id;
                   ELSE
                       -- Handle the duplicate case
                       duplicate_books := duplicate_books || (
                           SELECT jsonb_build_array(jsonb_build_object('book_id', book_id, 'book_name', book_name))
                           FROM books
                           WHERE book_name = b_name
                             AND author_name = a_name
                       );
                   END IF;
       
               EXCEPTION WHEN unique_violation THEN
                   -- Handle any other potential unique constraint violation (though this should be covered by ON CONFLICT)
                   duplicate_books := duplicate_books || (
                       SELECT jsonb_build_array(jsonb_build_object('book_id', book_id, 'book_name', book_name))
                       FROM books
                       WHERE book_name = b_name
                         AND author_name = a_name
                   );
               END;
           END LOOP;
       
           RETURN jsonb_build_object('inserted_books', inserted_books, 'duplicate_books', duplicate_books);
       END;
       $$;

    ```

    ```
    CREATE OR REPLACE FUNCTION fetch_recommended_books(p_user_id UUID, p_timestamp int8)
    RETURNS JSONB AS $$
    BEGIN
        RETURN coalesce((
            SELECT jsonb_agg(jsonb_build_object(
                'book_name', b.book_name,
                'author_name', b.author_name,
                'genre_tags', b.genre_tags,
                'description', b.description,
                'pages', b.pages,
                'isbn', b.isbn,
                'first_date_of_publication', b.first_date_of_publication,
                'reference_link', b.reference_link,
                'category_id', b.category_id,
                'category_name', c.category_name,
                'recommended_book_id', rb.id,
                'liked', rb.liked,
                'rating', rb.rating,
                'read', rb.read,
                'last_updated_time', rb.last_updated_time
            ))
            FROM recommendations r
            JOIN recommended_books rb ON r.recommendation_id = rb.recommendation_id
            JOIN books b ON rb.book_id = b.book_id
            JOIN categories c ON b.category_id = c.category_id
            WHERE r.user_id = p_user_id and  r.timestamp = p_timestamp 
        ),'[]'::jsonb);
    END;
    $$ LANGUAGE plpgsql;
    ```

    ```
    CREATE OR REPLACE FUNCTION fetch_recommended_books_by_id (p_id int8)
    RETURNS JSONB AS $$
    BEGIN
        RETURN coalesce((
            SELECT jsonb_agg(jsonb_build_object(
                'book_name', b.book_name,
                'author_name', b.author_name,
                'genre_tags', b.genre_tags,
                'description', b.description,
                'pages', b.pages,
                'isbn', b.isbn,
                'first_date_of_publication', b.first_date_of_publication,
                'reference_link', b.reference_link,
                'category_id', b.category_id,
                'category_name', c.category_name,
                'recommended_book_id', rb.id,
                'liked', rb.liked,
                'rating', rb.rating,
                'read', rb.read,
                'last_updated_time', rb.last_updated_time
            ))
            FROM recommendations r
            JOIN recommended_books rb ON r.recommendation_id = rb.recommendation_id
            JOIN books b ON rb.book_id = b.book_id
            JOIN categories c ON b.category_id = c.category_id
            WHERE r.recommendation_id = p_id
        ),'[]'::jsonb);
    END;
    $$ LANGUAGE plpgsql;
    ```

    - **Run the SQL Query**:
        - Click the `Run` button to execute the SQL query and deploy each RPC function.

    -  **Verify the Deployment**:
        - After the query runs successfully, verify that the RPC function `bulk_insert_books`, `fetch_recommended_books`,`fetch_recommended_books_by_id` has been created by checking the `Functions` section in the left-hand menu.

- Set up the ```OPENAI_API_KEY``` inside [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt)  for book information generation.

- Enable Google OAuth in Supabase for user authentication and also put Web client id from google cloud console inside Client ID (for oauth) in Supabase authentication and under [Constants.kt](../main/shared/src/commonMain/kotlin/utils/Constants.kt) file’s ```OAUTH_WEB_CLIENT_ID``` property in the shared module.
    - Refer configuration part of supabase guide for [android](https://supabase.com/docs/guides/auth/social-login/auth-google?queryGroups=platform&platform=android#using-google-sign-in-on-android) and [ios](https://supabase.com/docs/guides/auth/social-login/auth-google?queryGroups=platform&platform=swift#ios-configuration) if unfamilar with Google Oauth setup

3. Ktor Backend API setup (if using [bookmatch_backend](https://github.com/novumlogic/BookMatch/tree/bookmatch_backend))
- Configure `BOOKMATCH_BACKEND_URL` in which is configured to localhost initially `http:localhost:8080/generate-recommendations` to your hosted url
- Note:
    - Attribute `useClearTextTraffic` is set to true in [AndroidManifest.xml](https://github.com/novumlogic/BookMatch/blob/main/composeApp/src/androidMain/AndroidManifest.xml) due to local testing, make sure to make it false when using hosted url
    - Similarly property `NSAllowsArbitraryLoads` of `NSAppTransportSecurity` is set to true in [Info.plist](https://github.com/novumlogic/BookMatch/blob/main/iosApp/iosApp/Info.plist), make it false when using hosted url

4. Build the project:

- For Android:
```bash
./gradlew build 
```
- For iOS:
    - Run the following command ```./gradlew :shared:assembleXCFramework``` to convert the shared module into framework which needs to be included in iosApp.
    - Open iosApp.xcodeProj under iosApp module in Xcode
    - Refer [official guide](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-integrate-in-existing-app.html#make-your-cross-platform-application-work-on-ios) if facing any issue

5. Run the project:
- For Android:
```
./gradlew installDebug
```

- For iOS: Run the project from Xcode on a simulator or device.

## Blog post

For more information and insights like idea behind the app, challenge faced during the development, check out our Medium blog post:
- [Building BookMatch: A Journey into AI-Powered Book Recommendations](https://medium.com/novumlogic/building-bookmatch-a-journey-into-ai-powered-book-recommendations-035c0594a2f6)

To know more about how we built REST API for BookMatch using ktor, check out the following blog:
- [Building secure API gateway with Ktor](https://medium.com/novumlogic/building-a-secure-api-gateway-with-ktor-9129046cb3c7)

###  Contributing
We welcome contributions from the community. Please fork the repository and create a pull request with your changes.

*** 

Feel free to reach out if you have any questions or need further assistance. Enjoy discovering your next favorite book with BookMatch

