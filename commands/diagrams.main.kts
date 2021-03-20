#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:Repository("https://repo1.maven.org/maven2")
@file:DependsOn("org.diagramsascode:diagramsascode-image:0.1.1")
@file:DependsOn("org.diagramsascode:diagramsascode-core:0.1.1")
@file:DependsOn("org.diagramsascode:diagramsascode-activity:0.1.1")
@file:DependsOn("org.diagramsascode:diagramsascode-sequence:0.1.1")
@file:DependsOn("net.sourceforge.plantuml:plantuml:1.2021.1")
@file:DependsOn("com.github.yschimke:okurl-script:2.0.2")
@file:DependsOn("com.github.pgreze:kotlin-process:1.2")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.oksocial.output.handler.ConsoleHandler
import com.baulsupp.oksocial.output.responses.FileResponseExtractor
import org.diagramsascode.core.Diagram
import org.diagramsascode.image.SequenceDiagramImage
import org.diagramsascode.sequence.constraint.SequenceDiagramConstraints
import org.diagramsascode.sequence.edge.Message
import org.diagramsascode.sequence.node.Participant
import java.io.File

// Create the participants (that exchange messages)
val participant1 = Participant("Client")
val participant2 = Participant("Server")

// Create the request and response message
val message1 = Message(participant1, participant2, "Request Message")
val message2 = Message(participant2, participant1, "Response Message")

val diagram = Diagram.builder()
  .withNodes(participant1, participant2)
  .withEdges(message1, message2)
  .withConstraints(SequenceDiagramConstraints())
  .build()

var outputFile = File.createTempFile("sequence", ".png");
SequenceDiagramImage.of(diagram).writeToPngFile(outputFile);

println(outputFile)

ConsoleHandler.previewFile(outputFile)
