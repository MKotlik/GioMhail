# GioMhail
Stuy APCS1 Final Project, Mikhail Kotlik &amp; Giovanni Topa

## Description
GioMhail by Coolgle, is a barebones, console email client.
Like a simpler Mozilla Thunderbird, it allows users to securely view and send emails through any* email service.

\* Excludes Gmail, Hotmail, Outlook and any service that uses OAuth 2.0.

Features:
* Runs from a console, on any major OS
* Connects to secure ports on SMTP and POP protocol servers
* Supports 3 different authentication methods (behind-the-scenes) for logging into a server
* Provides basic functions for accessing a mailbox (show inbox, view emails (simplified & original views), delete emails)
* Provides basic functions for sending emails (user can specify Subject, From, To, CC, BCC, write email body, and save)
* Continues to work even as the user messes up or server/connection issues happen (lots of code checking user input, server responses, connection status, reporting on errors & providing guidance without failing)
 
Additional Tools:

SMTPConsole & POPConsole - 
We also have tools for establishing secure connections to SMTP or POP servers, respectively named SMTPConsole and POPConsole. These tools allow the user to manually enter commands to communicate with the servers, and are useful when troubleshooting connections or when developing for the email client. As of now, the connection info of the consoles can only be changed by editing the source code and recompiling.

## Running
* Download/clone the repo, move the "final" folder inside to any location of your choice.
* Open the terminal/shell of your choice and navigate to "final/compiled"
* Launch the script appropriate for your OS & shell.
    - Use "runClient-Linux-OSX.sh" for regular Bash shells on Linux or OSX computers
    - Use "runClient-Windows-Bash.sh" for Bash shells (ex. Cygwin, Git Shell) on Windows computers
    - Use "runClient-Windows.bat" for cmd.exe or Powershell on Windows computers

(Assuming you've installed the latest JVM)

## Instructions
Just follow the client's instructions! Helpful in-app prompts and command lists will guide you as you use it. It's that simple!

## Compiling
The project is precompiled, and a runnable version is within the final/compiled folder of the repo. However, if you have to recompile the project from source code:
* Navigate to src
* Launch the compile script appropriate for your OS & shell.
    - Use "compileClient-Linux-OSX.sh" for regular Bash shells on Linux or OSX computers
    - Use "compileClient-Windows-Bash.sh" for Bash shells (ex. Cygwin, Git Shell) on Windows computers
    - Use "compileClient-Windows.bat" for cmd.exe or Powershell on Windows computers

##Semi-completed Features
* We have code to read simple multipart messages and the outline of code to save the attachments in a file. However, upon doing more research we found that MIME was a lot more complex than we thought and that our code could not deal with some of the things it might have to (such as parts, within parts, within parts). Therefore, we are not including it in our final version although it is able to read simple MIME messages
* We also have code to download emails locally, however we ran out of time and were not able to test it
* We have code that should be able to read and parse a message from a locally stored file, however, we did not have time to test it and since we are not implementing the saving of emails, have excluded this as well

## Changelog
1/25:

Misha:
* Updated readme
* Researched MIME, realized we cannot make it work under time limit...
* Edited Inbox (removed Saved column since local email storage is not implemented)
* Fixed a bug where old header/server info (from previous/failed connection) would be displayed in Setup screen
* Added compile scripts to final/src
* Organized repo
 
Gio:
* Attempted to fix mime multipart message viewing

1/24:

Misha:
* Filled out ParseUtils
* Finished aligned/pretty Inbox listing in NewClient (w/ tons of helper methods)
* Finished email viewing
* Added option to choose between simple (no headers, parsed) and original (headers + body) view of message
* Added deletion feature (+ dialog)
* Reformatted files
* Created backup/final folder & run scripts

Gio:
* Worked on hashID(moved to HeaderStore)
* Created method to fill a msgClass from a file

1/23:

Misha:
* Researched MIME a bit
* Redesigned the Client graphics/layout
* Created logos for NewClient
* Implemented new layout in NewClient through printFrame methods, and by editing mode methods
* Impelmented logo support in NewClient through Storage
* Finalized Message HashID creation & methods (for linking local & server msgs)
* Created alignment helper methods
* Made method for parsing/editing email date header for inbox display

Gio:
* Researched MIME
* Wrote methods to read multipart messages based on MIME protocols and save attachments to local files

1/22:

Misha:
* Ported all POP functions from Client to NewClient. Updated all to new format/standard except Inbox & view.

Gio:
* Worked on saving an email from the server to a file

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

1/20:

Misha:
* Finished result menu in Client

Gio:
* Added integer argument checks in Client
* Began modularizing Client code
