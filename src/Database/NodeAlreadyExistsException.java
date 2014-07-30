package Database;

import Data.DataNode;

public class NodeAlreadyExistsException extends EntryAlreadyExistsException {
	
	private static final long serialVersionUID = -1793894108044155193L;

	public NodeAlreadyExistsException(NodesTable table, DataNode node) {
		super(table, node);
	}

	@Override
	public String toString() {
		return "Node \'" + ((DataNode)(getEntry())).getIpAddress() + "\' already exists in \'"
				+ getTable().getName() + "\' table.";
	}

	public DataNode getNode() {
		return (DataNode) getEntry();
	}
}
