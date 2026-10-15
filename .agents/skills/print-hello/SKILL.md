---
name: print-hello
description: '接收用户输入的文本参数，并生成一个 Python 脚本，打印 hello world 以及这个文本。'
---

# Print Hello

Use this skill when you need to create a simple Python script that prints a greeting with user-provided text.

## Instructions
1. Read the user-provided text argument. If none is provided, default to "world".
2. Create a file named `hello.py` in the working directory.
3. Add a short comment at the top of the script explaining what it does.
4. The script should print: `hello world: <text>`.
5. If the argument is missing, print: `hello world: world`.
6. Also provide the run command in the output, for example:
   - `python hello.py Alice`
   - `python hello.py`
7. Keep the script simple and runnable without extra dependencies.

## Example
Input:
`/print-hello Alice`

Output:
```python
# hello.py
# A simple script that prints hello world with a user-provided text.

import sys

text = sys.argv[1] if len(sys.argv) > 1 else "world"
print(f"hello world: {text}")
```

Run command:
```bash
python hello.py Alice
```
