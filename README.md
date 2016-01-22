# GioMhail
Stuy APCS1 Final Project, Mikhail Kotlik &amp; Giovanni Topa

## Description
GioMhail by Coolgle, is a barebones, console email client.
Like a simpler Mozilla Thunderbird, it allows users to securely view and send emails through any email service.

## Running
Open your favorite console and type: java Client. It's that simple!

(Assuming you've installed the latest JVM, downloaded the repo, and navigated to the folder.)

(If using source code, type javac *.java to compile all Java files in the source code folder, then run.)

## Instructions
Just follow the client's instructions! Helpful in-app prompts and command lists will guide you as you use it. It's that simple!

## Changelog

1/21:

Misha:
* Almost completed transition to NewClient (improved structure & functionality from Client)
    - Planned and designed NewClient
    - Finished instance vars, constructor, main, runLoop(), all SMTP mode methods, created methods for printing a frame, created getDraftDisplay() for displaying the message being edited, created methods for checking and parsing user input (getUserInput(), getIntElementUserInput(), getStrElementUserInput(), checkInputMatch(...)). Only POP mode methods remaining.
* Did some research on OAuth 2.0
 
Gio:
* Researched OAuth 2.0
* Downloaded OAuth 2.0 library
 
Goals:
* Finish NewClient
* Understand and implement OAuth 2.0 authentication in Session (for Gmail, Hotmail)
* Implement POP message storage, add downloading options NewClient
* Implement message deletion through POP in NewClient
* Implement MIME parsing and attachment download through POP

1/20 - Add things
