package engine.customdisruptor;


import command.Command;

//Simple mutable container class to be in ring buffer
public class ValueEvent {
    private Command command;

    public Command get(){
        return command;
    }

    public void set(Command value){
        this.command = value;
    }
}
