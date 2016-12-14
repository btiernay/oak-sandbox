package btiernay.jcr.convert;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.PropertyDefinition;

import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.BooleanValue;
import org.apache.jackrabbit.value.DecimalValue;
import org.apache.jackrabbit.value.DoubleValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.ByteStreams;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class NodeJsonConverter {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final JsonNodeFactory FACTORY = MAPPER.getNodeFactory();


	private static final boolean INCLUDE_METADATA = false;
	private static final boolean INCLUDE_STANDARD = true;

	@SneakyThrows
	public static ObjectNode convertNode(@NonNull Node node) {
		val result = MAPPER.createObjectNode();

		if (INCLUDE_METADATA) {
			addMetadata(node, result);
		}
		addProperties(node, result);
		addChildren(node, result);

		return result;
	}

	@SneakyThrows
	public static JsonNode convertProperty(Property property) {
		if (property.isMultiple()) {
			val values = MAPPER.createArrayNode();
			for (val value : property.getValues()) {
				values.add(convertValue(value));
			}

			return values;
		}
		
		return convertValue(property.getValue());
	}

	@SneakyThrows
	public static Value convertField(PropertyDefinition propertyDefinition, JsonNode fieldValue) {
		switch (propertyDefinition.getRequiredType()) {
		// Strings
		case PropertyType.STRING:
		case PropertyType.NAME:
		case PropertyType.REFERENCE:
		case PropertyType.WEAKREFERENCE:
		case PropertyType.PATH:
		case PropertyType.URI:
		case PropertyType.DATE:
			return new StringValue(fieldValue.textValue());

		// Others
		case PropertyType.BINARY:
			return new BinaryValue(fieldValue.binaryValue());
		case PropertyType.BOOLEAN:
			return new BooleanValue(fieldValue.booleanValue());
		case PropertyType.DECIMAL:
			return new DecimalValue(fieldValue.decimalValue());
		case PropertyType.DOUBLE:
			return new DoubleValue(fieldValue.doubleValue());
		case PropertyType.LONG:
			return new LongValue(fieldValue.longValue());
		case PropertyType.UNDEFINED:
		default:
			return null;
		}
	}

	@SneakyThrows
	public static JsonNode convertValue(@NonNull Value value) {
		val f = FACTORY;
		switch (value.getType()) {

		// Strings
		case PropertyType.STRING:
		case PropertyType.NAME:
		case PropertyType.REFERENCE:
		case PropertyType.WEAKREFERENCE:
		case PropertyType.PATH:
		case PropertyType.URI:
		case PropertyType.DATE:
			return f.textNode(value.getString());

		// Others
		case PropertyType.BINARY:
			return f.binaryNode(getBinary(value));
		case PropertyType.BOOLEAN:
			return f.booleanNode(value.getBoolean());
		case PropertyType.DOUBLE:
			return f.numberNode(value.getDouble());
		case PropertyType.DECIMAL:
			return f.numberNode(value.getDecimal());
		case PropertyType.LONG:
			return f.numberNode(value.getLong());
		case PropertyType.UNDEFINED:
		default:
			return f.nullNode();
		}
	}

	private static  byte[] getBinary(Value value) throws IOException, RepositoryException {
		return ByteStreams.toByteArray(value.getBinary().getStream());
	}


	private static void addMetadata(Node node, ObjectNode result) throws RepositoryException {
		result.put("jcr:path", node.getPath());
		result.put("jcr:name", node.getName());
	}

	private static void addProperties(Node node, ObjectNode result) throws RepositoryException, ValueFormatException {
		val properties = node.getProperties();
		while (properties.hasNext()) {
			val property = properties.nextProperty();
			val propertyName = property.getName();

			if (isExcluded(propertyName)) {
				continue;
			}

			result.set(propertyName, convertProperty(property));
		}
	}

	private static void addChildren(Node node, ObjectNode result) throws RepositoryException {
		val childNodes = node.getNodes();
		while (childNodes.hasNext()) {
			val childNode = childNodes.nextNode();

			val field = convertNode(childNode);
			val fieldName = childNode.getName();

			result.set(fieldName, field);
		}
	}

	private static boolean isExcluded(String propertyName) {
		return !INCLUDE_STANDARD && isStandard(propertyName);
	}

	private static boolean isStandard(String name) {
		return name.startsWith("jcr:") || name.startsWith("nt:");
	}

}
