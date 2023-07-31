# PERFECT PARSE COUNT: 21

# Python
subprocess.run(" java -jar run.jar ")

# Java
Process test = new ProcessBuilder("C:\\PathToExe\\MyExe.exe","param1","param2").start();

# C++
system("Program1 | Program2");
execlp("Program1", "Program1", NULL);
execlp("Program2", "Program2", NULL);
execl ("Program3", NULL);

# C#
Process.Start("process.exe");

# Golang
exec.Command("date")

# Ruby
system("echo", "*")
system({"rubyguides" => "best"}, "ruby", "-e p ENV['rubyguides']")
`ls`
%x|ls|
r = IO.popen("irb", "r+")

# Rust
let mut p = Popen::create(&["ps", "x"], PopenConfig {
    stdout: Redirection::Pipe, ..Default::default()
})?;
let dir_checksum = {
    Exec::shell("find . -type f") | Exec::cmd("sort") | Exec::cmd("sha1sum")
}.capture()?.stdout_str();

# Scala
# Executes "ls" and sends output to stdout
"ls".!
# Execute "ls" and assign a `Stream[String]` of its output to "contents".
val contents = Process("ls").lineStream
# Here we use a `Seq` to make the parameter whitespace-safe
def contentsOf(dir: String): String = Seq("ls", dir).!!

# JS
spawn('java',  ['-jar', '-Xmx512M', '-Dfile.encoding=utf8', 'script/importlistings.jar']);