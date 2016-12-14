package btiernay.jcr.oak.logging;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.spi.commit.CommitHook;
import org.apache.jackrabbit.oak.spi.commit.CommitInfo;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingCommitHook implements CommitHook {

	@Override
	public NodeState processCommit(NodeState before, NodeState after, CommitInfo info) throws CommitFailedException {
		log.info("*** before = {}, after = {}, info = {}", before, after, info);
		return after;
	}

}
