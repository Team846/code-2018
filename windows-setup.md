# Setting up WSL in Windows

Install Ubuntu through the Windows Store,
then open "Turn Windows features on or off".
Enable "Windows Subsystem for Linux", then restart
your computer.

Log back in, and open the "Ubuntu" app through Start,
or by running `ubuntu` in Command Prompt. At this point,
you should see "Installing, this may take a few minutes..."

After about 3-5 minutes, it will ask for a UNIX username.
You can use `robotics`, but any valid UNIX username will
do just fine. Enter a password for your account afterwards.

Now you have to install a ton of libraries to compile
scala native. Let's start:

**Install OpenJDK on WSL**
~~~
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt update
sudo apt-get install openjdk-8-jdk
sudo apt-get install openjdk-8-source
apt-cache search jdk
~~~

Set this to your `~/.profile`:
~~~
# ~/.profile: executed by the command interpreter for login shells.
# This file is not read by bash(1), if ~/.bash_profile or ~/.bash_login
# exists.
# see /usr/share/doc/bash/examples/startup-files for examples.
# the files are located in the bash-doc package.

# the default umask is set in /etc/profile; for setting the umask
# for ssh logins, install and configure the libpam-umask package.
#umask 022

# if running bash
if [ -n "$BASH_VERSION" ]; then
    # include .bashrc if it exists
    if [ -f "$HOME/.bashrc" ]; then
        . "$HOME/.bashrc"
    fi

fi

# set PATH so it includes user's private bin directories
PATH="$HOME/bin:$HOME/.local/bin:$PATH:/opt/idea-IU-173.4548.28/bin:$JAVA_HOME/bin"
export DISPLAY=:0
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
export NATIVE_TARGET = ARM32
~~~

Restart your Ubuntu shell. Ensure that the following commands all
echo something:

~~~
echo $JAVA_HOME # /usr/lib/jvm/java-8-openjdk
echo $PATH # [PATHS]:/opt/idea-IU-173.4548.28/bin:$JAVA_HOME/bin
echo $DISPLAY # :0
javac -version
~~~

Now, we are going to install all of the necessary tools for compilation:

**Install SBT*
~~~
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
~~~
**Install clang**
~~~
wget -O - https://apt.llvm.org/llvm-snapshot.gpg.key | sudo apt-key add -
sudo apt-add-repository "deb http://apt.llvm.org/xenial/ llvm-toolchain-xenial-5.0 main"
sudo apt-get update
sudo apt-get install -y clang-5.0
~~~
**Install the FRC toolchain**
~~~
sudo apt-add-repository ppa:wpilib/toolchain
sudo apt update 
sudo apt install frc-toolchain
~~~
**Install autoconf and bsdtar and libtool**
~~~
sudo apt install autoconf
sudo apt install bsdtar
sudo apt install libtool
~~~

Now, switch to a browser, and find a direct link to IntelliJ IDEA. At the moment,
it is <https://download.jetbrains.com/idea/ideaIU-2017.3.4.tar.gz>. To find this 
URL, press download on <https://jetbrains.com/idea>, cancel the resulting download,
and copy the link address for the "direct link".

Go back into Ubuntu, and run
~~~
wget https://download.jetbrains.com/idea/ideaIU-2017.3.4.tar.gz
sudo tar xf ideaIU-2017.3.4.tar.gz -C /opt
~~~

Your next task is to install XMing on Windows, which is fairly straightforward and
will not be documented, except for the note that you may want to check the boxes
for Quick Launch.

Make sure XMing is running in the background, by checking the tray area for its
icon.

Head back into Ubuntu, and 
~~~
cd [potassium]
sbt publishLocal
~~~

~~~
cd [code-2018]
sbt robotNative/nativeLink
~~~

And, if all went according to plan, your code should compile.