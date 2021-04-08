# bookmark-telegram-bot
[WiP] Telegram bot to bookmark URLs for future reading

## Abstract

The idea is to have a bot as a place to dump a link for future reading.

Later you can request a random link from this "backlog" and have an option to mark it read (= remove).

Optionally can have a means to tag links and then select by tag but need to find a way to make this having good usability in terms of chat interface.

## Use cases

### Add a link
|         |                 |
----------|------------------
**User**  |https://some.link/
**Bot**   | Ok, saved link. Links in backlog: 17
          
### Get random link
|         |                 |
----------|------------------
**User**  |`/random`
**Bot**   | https://some.link <BR> `/makr_read_123` `/random` 
          
### Mark read
|         |                 |
----------|------------------
**User**  |`/mark_read_123`
**Bot**   | `https://some.link` marked read `/undo_123`. <BR> Links in backlog: 16 `/random`  

### Undo
|         |                 |
----------|------------------
**User**  |`/undo_123`
**Bot**   | `https://some.link` marked unread `/mark_read_123`. <BR> Links in backlog: 17 `/random` 
