# Travel Buddy Android application

The application is built using Jetpack Compose and Material 3. 

## Development environment

Open the project in Android Studio.

## Project layout

![image](https://github.com/user-attachments/assets/a4f513f4-cd2e-44ef-96b5-86f9d18e82f6)

The `api` directory contains REST clients which connect to the Travel Buddy microservices. For the HTTP client, they use [Ktor Client](https://ktor.io/docs/client-create-new-application.html). 

The `model` directory contains models for data exchanged between the API and UI layers. All objects defined here should have the `@Serializable` attribute from `kotlinx.serialization`, so they can be (de)serialized by Ktor. 
Some of these models are also used for UI state holders. The global Ktor client is defined in `MainActivity.kt`, and all clients should use it.

The `ui` directory contains separate directories for each navigation route in the app. Every route/screen should have its own corresponding `ViewModel`, and its Composables. 
Do not call API functions from here directly, rather, provide callback arguments on certain events. The API clients should then be called in those callbacks.
