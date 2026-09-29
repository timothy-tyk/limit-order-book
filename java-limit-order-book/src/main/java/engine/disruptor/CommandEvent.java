package engine.disruptor;

import command.Command;

public final class CommandEvent {
    public Command command;
    public long createdAtNanos;

    public void clear(){
        command = null;
        createdAtNanos = 0;
    }
}
