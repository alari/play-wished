play-wished
===========

Simple wrapper for Play Framework 2 in-app exceptions

How do you handle unwished paths in your Play application?
-----------

You may wrap your syncronous code with try-catch in every action or in business layer.

Or you could add `recover` block after every future.

It makes your code to look like a mess.

Well, you may catch errors in `Global`, but what if it's not a failure, but just a unwished, non-happy, but ordinary control path? Then why should you get this stacktrace in logs, what is it for?

So, the best way is to create your custom `Action`, with `try`-`catch` for the blocks and `recover` for the futures. Then you need to decide, what to do with exceptions...

That's what is this project for
-----------

It contains of a custom `WishedAction`, a `Wished.wrap` method for your special `Action`s, a `Unwished` type with `Content` to render and a number of predefined exceptions.

So that you could `throw Unwished.BadRequest` (it's a val) or `throw Unwished.InsufficientStorage("Buy more space!")` (with custom content to render).

Other libraries could inherit from `Unwished` and provide response status codes.

Handling unhappy, unwished paths is easy again ;)
