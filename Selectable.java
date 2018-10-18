package mycontroller;

/* used to determine whether the controller should select it (the tile) */
public interface Selectable {
	public boolean goodToSelect(MyAIController controller);
}
