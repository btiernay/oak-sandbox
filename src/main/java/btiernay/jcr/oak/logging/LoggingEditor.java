package btiernay.jcr.oak.logging;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.commit.Editor;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  class LoggingEditor implements Editor {
  
	@Override
	public void leave(NodeState before, NodeState after) throws CommitFailedException {
		log.info("[leave]");
	}

	@Override
	public void enter(NodeState before, NodeState after) throws CommitFailedException {
		log.info("[enter]");
	}

	@Override
	public void propertyDeleted(PropertyState before) throws CommitFailedException {
		log.info("[propertyDeleted]");
	}

	@Override
	public void propertyChanged(PropertyState before, PropertyState after) throws CommitFailedException {
		log.info("[propertyChanged]");
	}

	@Override
	public void propertyAdded(PropertyState after) throws CommitFailedException {
		log.info("[propertyAdded]");
	}

	@Override
	public Editor childNodeDeleted(String name, NodeState before) throws CommitFailedException {
		log.info("[childNodeDeleted:{}]", name);
		return this;
	}

	@Override
	public Editor childNodeChanged(String name, NodeState before, NodeState after)
			throws CommitFailedException {
		log.info("[childNodeChanged:{}]", name);
		return this;
	}

	@Override
	public Editor childNodeAdded(String name, NodeState after) throws CommitFailedException {
		log.info("[childNodeAdded:{}]", name);
		return this;
	}
	
}