package btiernay.jcr.oak.logging;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.commit.Validator;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  class LoggingValidator implements Validator {
  
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
	public Validator childNodeDeleted(String name, NodeState before) throws CommitFailedException {
		log.info("[childNodeDeleted:{}]", name);
		return this;
	}

	@Override
	public Validator childNodeChanged(String name, NodeState before, NodeState after)
			throws CommitFailedException {
		log.info("[childNodeChanged:{}]", name);
		return this;
	}

	@Override
	public Validator childNodeAdded(String name, NodeState after) throws CommitFailedException {
		log.info("[childNodeAdded:{}]", name);
		return this;
	}
	
}