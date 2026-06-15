![Build: 150626](https://img.shields.io/badge/Build-150626-green.svg)


# INTRODUCTION

[EditiX XML Editor](https://www.editix.com) is a powerful, open-source XML Editor : 

- Elegant, user-intuitive interface
- Smart on-the-fly syntax helper
- XSLT Editor and Debugger
- Visual W3C Schema Editor
- XML Project Management
- Complete XML Workflow

 ![Screen 1 for EditiX XML Editor](images/editix1.png)

 ![Screen 2 for EditiX XML Editor](images/editix2.png)


# LICENSES

## 1) Non commercial with GPL 3

EditiX is licensed under the **[GPL-3.0](LICENSE)**. This means:

**You can modify the code** for **non-commercial projects**.
**You can redistribute** modified versions **under GPL-3.0** (with source code).

A commercial license is required to integrate EditiX into proprietary software

## 2) Commercial usage

For **closed-source projects** or **commercial use without GPL constraints**, a commercial license is available:

This allows integration into proprietary software and closed-source projects.

**[Purchase a Commercial License](https://www.editix.com)**

# SERVICE

Using Editix XML Editor and need a professional adaptation? I offer tailored services to customize, optimize, and integrate Editix into your workflow. [Let’s talk about your needs!](https://www.editix.com)

# INSTALLATION

This program requires Java 8 or later.

## Windows

Install git for your machine at [https://git-scm.com/install](https://git-scm.com/install)

Install a java jdk for your machine at [https://www.oracle.com/java/technologies/downloads](https://www.oracle.com/java/technologies/downloads)

Install ant (for compiling/running all) for your machine at [https://ant.apache.org/ivy/download.cgi](https://ant.apache.org/ivy/download.cgi)

Get the EditiX repository from the command line with

```bash
git clone https://github.com/AlexandreBrillant/Editix-xml-editor
```

Compile the EditiX XML Editor Source with 

```bash
ant compile
```

By default EditiX is compiled under Java 8, if you want another version (like Java 7), juste update the *build.xml* updating the *"source"* and *"target"* attributes (like 1.7) for the *javac* command.

Now you can run EditiX XML Editor with

```bash
ant run
```

Or using

```bash
run.bat
```

## Linux/Ubuntu

(You may replace 17 for the openjdk by 18,19...)

```bash
sudo apt install openjdk-17-jre
sudo apt install openjdk-17-jdk
sudo apt install ant
git clone https://github.com/AlexandreBrillant/Editix-xml-editor
ant compile
ant run
```

If you see the following error "java.awt.AWTError : Assistive Technology not found..." then edit
the /etc/java-8-openjdk/accessibility.properties (update 8 by your java version) and put a (#) comment for the line "assistive_technologies=..."

## Linux/Debian

[YouTube video from Renzo de Paoli](https://www.youtube.com/watch?v=pQA5nD2OGKM)


# CONTACT

For questions or support, contact me at : [https://www.editix.com](https://www.editix.com) or at
my professional web site : [https://www.alexandrebrillant.com](https://www.alexandrebrillant.com)

# Copyright

Copyright (c) 2026 Alexandre Brillant. All rights reserved.
