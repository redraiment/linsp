# Linsp

**Lisp in Lisp** - A minimalistic Lisp interpreter crafted with Kotlin/Native, Linsp provides the foundational operations described by Paul Graham in his seminal article *[The Root of Lisp](http://www.paulgraham.com/rootsoflisp.html)*. This interpreter offers the seven atomic operations: `quote`, `atom`, `eq`, `car`, `cdr`, `cons`, and `cond`, along with support for `lambda`.

### Key Features

- **Minimalistic Design**: Linsp is built with simplicity in mind, focusing on the core principles of Lisp.
- **Lambda Support**: Enables the creation of anonymous functions with lexical scope.
- **Extended Functionality**: Beyond the seven atomic operations, Linsp introduces new operators such as `first`, `second`, `next`, `=`, and `if`, enriching the language's capabilities.

### Quick Start

To get started with Linsp, follow these simple steps:

1. Clone the Linsp repository:
   ```sh
   git clone https://github.com/redraiment/linsp.git
   ```
2. Change to the Linsp directory:
    ```sh
    cd linsp
    ```
3. Build and link the native executable using Gradle:
    ```sh
    ./gradlew linkNative
    ```
4. Run the Linsp interpreter with the main script:
    ```sh
    build/bin/native/releaseExecutable/linsp.kexe main.linsp
    ```

### Approach

The development approach of Linsp is reminiscent of PyPy, focusing on a clean
and efficient implementation. Further details on the implementation will be
provided in future updates.

### Contributing

We welcome contributions to Linsp! For bug reports, feature requests, or questions,
please create an issue or submit a pull request.

### License

Linsp is open-source software, released under the MIT License.

Feel free to explore, learn, and extend the Linsp interpreter to suit your needs.
