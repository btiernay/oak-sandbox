package btiernay.jcr.oak.logging;

import org.apache.jackrabbit.oak.spi.commit.CommitInfo;
import org.apache.jackrabbit.oak.spi.commit.Observer;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingObserver implements Observer {

	@Override
	public void contentChanged(NodeState root, CommitInfo info) {
		log.info("*** Content Changed: root = {}, info = {}", root, info);
	}

}
