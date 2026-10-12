package factorysim.model;

import factorysim.config.MachineConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Machine requirements and statistics.
 */
public class MachineTest {

    private static MachineConfig processorConfig(
        String name,
        String inputItem,
        int inputAmount,
        String outputItem,
        int outputAmount,
        int cooldown
    ) {
        return new MachineConfig(name, cooldown)
            .addInput(inputItem, inputAmount, "in_belt")
            .addOutput(outputItem, outputAmount, "out_belt");
    }

    private static MachineConfig mineConfig(
        String name,
        String outputItem,
        int outputAmount,
        int cooldown
    ) {
        return new MachineConfig(name, cooldown)
            .addOutput(outputItem, outputAmount, "out_belt");
    }

    @Test
    public void mineCanActivateWithoutInputs() {
        Machine machine = new Machine(
            mineConfig("Iron Mine", "iron_ore", 1, 2)
        );

        assertTrue(machine.hasRequiredInputs());
        assertTrue(machine.canActivate());

        machine.tick();

        OutputPort output = machine.getOutputPorts().get(0);
        assertEquals(1, output.getItemsStored());
        assertEquals(2, machine.getCooldownRemaining());
        assertEquals(1.0, machine.getUtilisation());
    }

    @Test
    public void machineDoesNotActivateWhenInputIsMissing() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 2, "iron_plate", 1, 1)
        );

        assertFalse(machine.hasRequiredInputs());
        assertFalse(machine.canActivate());

        machine.tick();

        OutputPort output = machine.getOutputPorts().get(0);
        assertEquals(0, output.getItemsStored());
        assertEquals(0, machine.getCooldownRemaining());
        assertEquals(1, machine.getTicksSinceReset());
        assertEquals(0, machine.getUtilisedTicks());
        assertEquals(0.0, machine.getUtilisation());
    }

    @Test
    public void machineActivatesWhenInputsAreFullAndOutputsAreEmpty() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 2, "iron_plate", 1, 1)
        );

        InputPort input = machine.getInputPorts().get(0);
        input.pushItem("iron_ore");
        input.pushItem("iron_ore");

        assertTrue(machine.hasRequiredInputs());
        assertTrue(machine.hasEmptyOutputs());
        assertTrue(machine.canActivate());

        machine.tick();

        OutputPort output = machine.getOutputPorts().get(0);
        assertEquals(0, input.getItemsStored());
        assertEquals(1, output.getItemsStored());
        assertEquals(1, machine.getCooldownRemaining());
        assertEquals(1.0, machine.getUtilisation());
    }

    @Test
    public void machineCannotActivateWhenOutputIsNotEmpty() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 1, "iron_plate", 1, 0)
        );

        InputPort input = machine.getInputPorts().get(0);
        OutputPort output = machine.getOutputPorts().get(0);

        input.pushItem("iron_ore");
        machine.tick();

        assertEquals(1, output.getItemsStored());

        input.pushItem("iron_ore");
        machine.tick();

        assertEquals(1, output.getItemsStored());
        assertEquals(1, input.getItemsStored());
        assertEquals(2, machine.getTicksSinceReset());
        assertEquals(1, machine.getUtilisedTicks());
        assertEquals(0.5, machine.getUtilisation());
    }

    @Test
    public void cooldownPreventsImmediateReactivation() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 1, "iron_plate", 1, 2)
        );

        InputPort input = machine.getInputPorts().get(0);
        OutputPort output = machine.getOutputPorts().get(0);

        input.pushItem("iron_ore");
        machine.tick();

        assertEquals(1, output.getItemsStored());
        assertEquals(2, machine.getCooldownRemaining());

        output.pullItem();
        input.pushItem("iron_ore");

        machine.tick();

        assertEquals(0, output.getItemsStored());
        assertEquals(1, machine.getCooldownRemaining());

        machine.tick();

        assertEquals(0, output.getItemsStored());
        assertEquals(0, machine.getCooldownRemaining());

        machine.tick();

        assertEquals(1, output.getItemsStored());
        assertEquals(2, machine.getCooldownRemaining());
    }

    @Test
    public void utilisationCountsActivationAndCooldownButNotBlockedTicks() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 1, "iron_plate", 1, 1)
        );

        machine.tick();

        assertEquals(0.0, machine.getUtilisation());

        InputPort input = machine.getInputPorts().get(0);
        input.pushItem("iron_ore");

        machine.tick();

        assertEquals(2, machine.getTicksSinceReset());
        assertEquals(1, machine.getUtilisedTicks());
        assertEquals(0.5, machine.getUtilisation());
    }

    @Test
    public void resetStatisticsClearsOnlyStatistics() {
        Machine machine = new Machine(
            processorConfig("Smelter", "iron_ore", 1, "iron_plate", 1, 1)
        );

        InputPort input = machine.getInputPorts().get(0);
        OutputPort output = machine.getOutputPorts().get(0);

        input.pushItem("iron_ore");
        machine.tick();

        assertEquals(1, output.getItemsStored());
        assertEquals(1, machine.getCooldownRemaining());
        assertEquals(1.0, machine.getUtilisation());

        machine.resetStatistics();

        assertEquals(1, output.getItemsStored());
        assertEquals(1, machine.getCooldownRemaining());
        assertEquals(0, machine.getTicksSinceReset());
        assertEquals(0, machine.getUtilisedTicks());
        assertEquals(0.0, machine.getUtilisation());
    }

    @Test
    public void multipleInputsMustAllBeFullBeforeActivation() {
        Machine machine = new Machine(
            new MachineConfig("Concrete Mixer", 1)
                .addInput("iron_ore", 1, "iron_belt")
                .addInput("coal", 2, "coal_belt")
                .addOutput("concrete", 1, "out_belt")
        );

        InputPort ironInput = machine.getInputPorts().get(0);
        InputPort coalInput = machine.getInputPorts().get(1);

        ironInput.pushItem("iron_ore");
        coalInput.pushItem("coal");

        assertFalse(machine.hasRequiredInputs());
        assertFalse(machine.canActivate());

        machine.tick();

        assertEquals(0, machine.getOutputPorts().get(0).getItemsStored());

        coalInput.pushItem("coal");

        assertTrue(machine.hasRequiredInputs());
        assertTrue(machine.canActivate());

        machine.tick();

        assertEquals(1, machine.getOutputPorts().get(0).getItemsStored());
        assertEquals(0, ironInput.getItemsStored());
        assertEquals(0, coalInput.getItemsStored());
    }
}