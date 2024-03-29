[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner-direct-single.svg)](https://stand-with-ukraine.pp.ua)

# bookmark-telegram-bot

Telegram bot to bookmark URLs for future reading

[t.me/LinkDumpBot](http://t.me/LinkDumpBot)

## Abstract

The idea is to have a bot as a place to dump a link for future reading.

Later you can request a random link from this "backlog" and have an option to mark it read (= remove).

Optionally can have a means to tag links and then select by tag but need to find a way to make this having good usability in terms of chat interface.

## Use cases

### Add a link
|          |                                                |
|----------|------------------------------------------------|
| **User** | https://some.link/                             |
| **Bot**  | Ok, saved link. Links in backlog: 17 `/random` |

or (link already in DB)

|         |                                                                        |
|---------|------------------------------------------------------------------------|
| **Bot** | ⚠️ Already in backlog `/mark_read_123`. Links in backlog: 16 `/random` |

### Get random link
|          |                                                   |
|----------|---------------------------------------------------|
| **User** | `/random`                                         |
| **Bot**  | https://some.link <BR> `/mark_read_123` `/random` |

### Mark read
|          |                                                                                  |
|----------|----------------------------------------------------------------------------------|
| **User** | `/mark_read_123`                                                                 |
| **Bot**  | `https://some.link` marked read `/undo_123`. <BR> Links in backlog: 16 `/random` |

### Undo
|          |                                                                                         |
|----------|-----------------------------------------------------------------------------------------|
| **User** | `/undo_123`                                                                             |
| **Bot**  | `https://some.link` marked unread `/mark_read_123`. <BR> Links in backlog: 17 `/random` |
