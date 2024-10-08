Debuging a Swing application with Idea is a little tricky.

First, add to your build.grade.kts

 import org.springframework.boot.gradle.tasks.run.BootRun

 tasks.withType<BootRun> {
         jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
 }



Then start the application with "bootrun". Now you can append the debugger in Idea with "Run -> Attach to Process..."
