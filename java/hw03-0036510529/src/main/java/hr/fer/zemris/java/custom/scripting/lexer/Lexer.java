package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Separates String input in to smaller segments
 * 
 * @author Benjamin Kušen
 *
 */
public class Lexer {

	/**
	 * data of input
	 */
	private char[] data;
	/**
	 * current token
	 */
	private Token token;
	/**
	 * current index
	 */
	private int currentIndex;
	/**
	 * State in which lexer operates, can be tag or string
	 */
	private LexerState state;

	/**
	 * Receives input and initalizes data array.
	 * 
	 * @param input
	 */
	public Lexer(String input) {
		if (input == null) {
			throw new NullPointerException();
		}
		data = input.toCharArray();
		currentIndex = 0;
		state = LexerState.STRING;
	}

	/**
	 * changes state in which lexer works.
	 * 
	 * @param state
	 */
	public void setState(LexerState state) {
		this.state = state;
	}

	/**
	 * Generates new token from data array and returns it.
	 * 
	 * @return next token of input
	 */
	public Token nextToken() {
		if (token != null && token.getType() == TokenType.EOF) {
			throw new LexerException();
		}
		if (data.length == 0 || currentIndex == data.length) {
			token = new Token(TokenType.EOF, null);
			return token;
		}
		if (state == LexerState.TAG) {
			skipWhiteSpaces();
		}
		if (currentIndex + 1 < data.length && data[currentIndex] == '{' && data[currentIndex + 1] == '$') { // Checks if
																											// tag is
																											// opening
			token = new Token(TokenType.TAG, "{$");
			currentIndex += 2;
			return token;
		} else if (currentIndex + 1 < data.length && data[currentIndex] == '$' && data[currentIndex + 1] == '}') { // Checks
																													// if
																													// tag
																													// is
																													// closing
			token = new Token(TokenType.TAG, "$}");
			currentIndex += 2;
			return token;
		} else if (state == LexerState.STRING) { // Checks if it is in string state
			return stringLexer();
		} else {
			return tag();
		}

	}

	/**
	 * Returns last token generated by lexer.
	 * 
	 * @return current token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Generates Token that is string type.
	 * 
	 * @return Token with string type
	 */
	private Token stringLexer() { // returns string
		StringBuilder builderString = new StringBuilder();

		while (currentIndex < data.length) {
			if (currentIndex + 1 < data.length && data[currentIndex] == '\\'
					&& (data[currentIndex + 1] != '\\' || data[currentIndex] != '{')) {
				throw new LexerException();
			} else if (currentIndex + 1 < data.length && data[currentIndex] == '{' && data[currentIndex + 1] == '$') {
				token = new Token(TokenType.STRING, builderString.toString());
				return token;
			} else {
				builderString.append(data[currentIndex]);
				currentIndex++;
			}
		}
		token = new Token(TokenType.STRING, builderString.toString());
		return token;
	}

	/**
	 * Generates a token that can be number, string, variable or operator.
	 * 
	 * @return token
	 */
	private Token tag() { // returns input interpreted as within a tag
		StringBuilder builderString = new StringBuilder();

		skipWhiteSpaces();

		if (data[currentIndex] == '=') { // Checks if it is echo type
			builderString.append(data[currentIndex]);
			currentIndex++;
			token = new Token(TokenType.EQUALSIGN, builderString.toString());
			return token;
		} else if (data[currentIndex] == '"') { // Checks if first index points to "
			builderString.append(data[currentIndex]);
			currentIndex++;
			while (currentIndex + 1 < data.length && data[currentIndex] != '$' && data[currentIndex + 1] != '}') {
				if (data[currentIndex] == '"') {
					builderString.append(data[currentIndex]);
					currentIndex++;
					break;
				}
				builderString.append(data[currentIndex]);
				currentIndex++;
			}
			token = new Token(TokenType.STRING, builderString.toString());
			return token;
		} else if (currentIndex + 1 < data.length // Checks if minus is interpreted as part of a number
				&& (data[currentIndex] == '-' && Character.isDigit(data[currentIndex + 1]))
				|| Character.isDigit(data[currentIndex])) {
			builderString.append(data[currentIndex]);
			currentIndex++;
			int dotCounter = 0;
			while (currentIndex + 1 < data.length && data[currentIndex] != '$' && data[currentIndex + 1] != '}') {
				if (Character.isDigit(data[currentIndex])) {
					builderString.append(data[currentIndex]);
					currentIndex++;
				} else if (dotCounter == 1) {
					break;
				} else if (data[currentIndex] == '.') {
					builderString.append(data[currentIndex]);
					currentIndex++;
					dotCounter++;
				} else {
					break;
				}
			}
			if (builderString.toString().contains(".")) { // Checks if number has more than one dot
				try {
					token = new Token(TokenType.CONSTANT_DOUBLE, Double.parseDouble(builderString.toString()));
					return token;
				} catch (NumberFormatException e) {
					throw new LexerException();
				}
			} else {
				try {
					token = new Token(TokenType.CONSTANT_INTEGER, Integer.parseInt(builderString.toString()));
					return token;
				} catch (NumberFormatException e) {
					throw new LexerException();
				}
			}
		} else if (isOperator(data[currentIndex])) { // Checks if currentIndex points to operator
			builderString.append(data[currentIndex]);
			currentIndex++;
			token = new Token(TokenType.OPERATOR, builderString.toString());
			return token;
		} else if (data[currentIndex] == '@') { // Checks for function
			currentIndex++;
			token = new Token(TokenType.FUNCTION, isValidFunctionOrVariableString());
			return token;
		} else if (Character.isLetter(data[currentIndex])) {
			token = new Token(TokenType.VARIABLE, isValidFunctionOrVariableString());
			return token;
		} else {
			throw new LexerException();
		}
	}

	/**
	 * Returns segment of data as a string that meets the conditions of this lexer.
	 * 
	 * @return segment of data as string
	 */
	private String isValidFunctionOrVariableString() {
		StringBuilder builderString = new StringBuilder();

		while (currentIndex + 1 < data.length && data[currentIndex] != '$' && data[currentIndex + 1] != '}') {
			if (!Character.isLetter(data[currentIndex]) && !Character.isDigit(data[currentIndex])
					&& data[currentIndex] != '_') {
				break;
			} else {
				builderString.append(data[currentIndex]);
				currentIndex++;
			}
		}
		return builderString.toString();
	}

	/**
	 * Returns true if operator is {+, -, *, /, ^};
	 * 
	 * @param operator
	 * @return true if input is mathematical operator, false otherwise
	 */
	private boolean isOperator(char operator) {
		if (operator == '+' || operator == '*' || operator == '/' || operator == '-' || operator == '^') {
			return true;
		}
		return false;
	}

	/**
	 * Skips white spaces.
	 * 
	 */
	private void skipWhiteSpaces() {

		while (currentIndex < data.length && (data[currentIndex] == '\r' || data[currentIndex] == '\n'
				|| data[currentIndex] == '\t' || data[currentIndex] == ' ')) {
			currentIndex++;
		}

	}

}
