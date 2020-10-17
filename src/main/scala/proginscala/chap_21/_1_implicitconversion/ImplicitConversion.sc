import java.awt.event.{ActionEvent, ActionListener}

import javax.swing.JButton

val button = new JButton()
//button.addActionListener(
//  new ActionListener {
//    override def actionPerformed(e: ActionEvent): Unit = {
//      println("pressed!")
//    }
//  }
//)
//button.addActionListener(
//  (_: ActionEvent) => println("pressed!")
//)

implicit def function2ActionListener(f: ActionEvent => Unit) =
  new ActionListener {
    override def actionPerformed(event: ActionEvent): Unit = f(event)
  }

button.addActionListener(
  (_: ActionEvent) => println("pressed!")
)
