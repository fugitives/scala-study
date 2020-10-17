package proginscala.chap_18._5_simulation

object MySimulation extends CircuitSimulation {
  override def InverterDelay = 1

  override def AndGateDelay = 3

  override def OrGateDelay = 5
}
