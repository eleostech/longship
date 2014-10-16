# longship

A little library designed make it easier to work with the Erlang/OTP JInterface in Clojure.

## Usage

### A silly example

First, start EPDM:

```bash
epmd -d
```

Then, in a REPL:

```clojure
(require 'longship.server)
(use '[longship.otp :only [tuple]])

(longship.server/defhandler greet [name]
  (str "What ho, " name "!"))

(longship.server/defhandler add [& numbers]
  (tuple :ok (apply + numbers)))

(longship.server/start-server "horsesaredelicious@localhost" "messagebox" "cookie")
```

Then, start an erlang node with the same cookie:

```bash
erl -boot start_sasl -s crypto -sname littlebean -setcookie cookie
```

Then, from the Erlang shell:

```erlang
> Node = {messagebox, list_to_atom("horsesaredelicious@localhost")}.
> Node ! {self(), greet, "Sir Moosemeat McStanley, Keeper of the Badger-Knights of Hamramnon"}.
{<0.49.0>,greet,"Sir Moosemeat McStanley, Keeper of the Badger-Knights of Hamramnon"}
> receive MyGreeting -> MyGreeting end.
"What ho, Sir Moosemeat McStanley, Keeper of the Badger-Knights of Hamramnon!"
> Node ! {self(), add, 1, 2, 3, 4, 5}.
{<0.49.0>,add,1,2,3,4,5}.
> receive MySum -> MySum end.
{ok,15}.
```

## License

Copyright © 2014 Ryan Crum

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
