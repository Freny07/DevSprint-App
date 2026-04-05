# 🌀 Identity-Based Decision App

## 🚀 Overview

This application is a single-screen Android app that creatively combines multiple unrelated APIs into a cohesive, story-driven experience.

The app generates a random identity, assigns a visual avatar, evaluates the user with a trivia question, introduces a random pet, and finally determines whether the user is allowed to keep the pet.

---

## 🎯 Concept

The goal of this project is to transform unrelated API responses into a meaningful and interactive flow:

* Generate a new identity
* Represent it visually
* Introduce a challenge
* Assign a reward (pet)
* Conclude with a decision

This creates a structured, engaging user journey from randomness.

---

## 🔌 APIs Used

| Category | API                                     | Purpose                    |
| -------- | --------------------------------------- | -------------------------- |
| Utility  | https://randomuser.me/api/              | Generates random identity  |
| Creative | https://robohash.org/                   | Generates avatar from name |
| Fandom   | https://opentdb.com/api.php?amount=1    | Provides trivia question   |
| Animals  | https://dog.ceo/api/breeds/image/random | Generates random dog       |
| Humor    | https://yesno.wtf/api                   | Provides final decision    |

---

## ⚙️ Application Flow

1. **Identity Generation**

   * Fetch user data (name, gender, nationality)
   * Create avatar using Robohash

2. **Identity Display**

   * Present user details along with generated avatar

3. **Trivia Interaction**

   * Display a randomly generated trivia question

4. **Pet Assignment**

   * Fetch and display a random dog image

5. **Final Decision**

   * Use Yes/No API to determine if the user can keep the pet

---

## ✨ Features

* Single-screen, structured UI
* Real-time API integration
* Dynamic avatar generation
* Sequential user flow
* Lightweight and fast execution

---

## 🏗️ Tech Stack

* **Language:** Kotlin
* **Platform:** Android
* **Architecture:** Basic Activity-based structure
* **Networking:** REST API calls
* **Image Handling:** Glide/Picasso (if implemented)

---

## 🧪 Screenshots

<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/0faac4b4-b7c4-4767-875c-7eedad755095" />
<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/8d7e8662-046f-46fe-a996-8fbd443b34c2" />
<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/928ac806-9f0d-4e12-b77d-485fffb5fcdb" />



---

## 🎥 Demo Video

https://youtube.com/shorts/BgHd0RCnI_c

---

## 📦 APK

https://drive.google.com/file/d/10GqJMDvbzbePWw6JnlCxn4QvlHacXjOf/view?usp=drive_link

---

## 🏁 Conclusion

This project demonstrates how seemingly unrelated APIs can be combined into a cohesive and engaging application through thoughtful design and structured flow.

It emphasizes creativity, integration, and simplicity within a constrained development time.
