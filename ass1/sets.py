#!/usr/bin/env python3

from pysat.examples.hitman import Hitman

if __name__ == "__main__":
    try:
        while True:
            sets = eval(input("Enter a set of sets: "))
            with Hitman(bootstrap_with=sets, htype='sorted') as hitman:
                for hs in hitman.enumerate():
                    print(hs)
    except KeyboardInterrupt:
        pass
