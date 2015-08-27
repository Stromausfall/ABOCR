package net.matthiasauer.abocr.utils.state;

public enum StateEnum {
	/**
	 * can not be claimed, will not be removed
	 * --> the component is being prepared
	 */
	Preparing,
	/**
	 * can be claimed, will be removed
	 * --> the component has been prepared and waits for a system to take it
	 */
	Unclaimed,
	/**
	 * is claimed, will not be removed
	 * --> exists to save the state of the State System
	 */
	Claimed,
	/**
	 * The component has been flagged to be put into Finished state
	 * --> the creator system can then decide if it wants to finish the component
	 * (not implemented yet)
	 */
	RequestFinished,
	/**
	 * is claimed, will be removed
	 * --> the state is not valid anymore - it will be removed
	 */
	Finished
}
