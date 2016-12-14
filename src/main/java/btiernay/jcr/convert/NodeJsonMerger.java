package btiernay.jcr.convert;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.SneakyThrows;
import lombok.val;

/**
 * Recursively merges two a node and its updates.
 */
public class NodeJsonMerger {

	public void merge(Node target, ObjectNode node) {
		traverse(target, node);
	}

	@SneakyThrows
	private void traverse(Node target, JsonNode node) {
		val fieldNames = node.fieldNames();

		while (fieldNames.hasNext()) {
			val fieldName = fieldNames.next();
			val field = node.get(fieldName);

			//
			// Update
			//

			{
				// Property
				if (target.hasProperty(fieldName)) {
					val propertyDefinition = target.getProperty(fieldName).getDefinition();

					setProperty(target, fieldName, field, propertyDefinition);
					continue;
				}

				// Node
				if (target.hasNode(fieldName)) {
					val childNode = target.getNode(fieldName);

					// Recurse
					traverse(childNode, field);
					continue;
				}
			}

			//
			// Create
			//

			{
				val nodeTypes = target.getDefinition().getRequiredPrimaryTypes();

				// Property
				val propertyDefinition = findPropertyDefinition(nodeTypes, fieldName);
				if (propertyDefinition != null) {
					setProperty(target, fieldName, field, propertyDefinition);
					continue;
				}

				// Node
				val childNodeDefinition = findChildNodeDefinition(nodeTypes, fieldName);
				if (childNodeDefinition != null) {
					val childNode = target.addNode(fieldName, childNodeDefinition.getDefaultPrimaryTypeName());

					// Recurse
					traverse(childNode, field);
					continue;
				}
			}

			// Ignore

			{
				// If unrecognized fields come in, just drop them (...for now)
			}
		}
	}

	@SneakyThrows
	private static void setProperty(Node target, String fieldName, JsonNode field,
			PropertyDefinition propertyDefinition) {
		target.setProperty(fieldName, NodeJsonConverter.convertField(propertyDefinition, field));
	}

	private static PropertyDefinition findPropertyDefinition(NodeType[] nodeTypes, String propertyName) {
		for (val nodeType : nodeTypes)
			for (val propertyDefinition : nodeType.getPropertyDefinitions())
				if (propertyDefinition.getName().equals(propertyName))
					return propertyDefinition;

		return null;
	}

	private static NodeDefinition findChildNodeDefinition(NodeType[] nodeTypes, String childNodeName) {
		for (val nodeType : nodeTypes)
			for (val childNodeDefinition : nodeType.getChildNodeDefinitions())
				if (childNodeDefinition.getName().equals(childNodeName))
					return childNodeDefinition;

		return null;
	}

}
