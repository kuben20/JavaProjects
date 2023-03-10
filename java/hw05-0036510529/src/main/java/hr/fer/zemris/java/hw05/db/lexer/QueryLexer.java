package hr.fer.zemris.java.hw05.db.lexer;

import java.util.Objects;

/**
 * Tokenizes given input
 * 
 * @author Benjamin Kušen.
 *
 */
public class QueryLexer {
	/**
	 * Input
	 */
	private char[] data;
	/**
	 * Last generated token.
	 */
	private Token token;
	/**
	 * Last character analyzed in data.
	 */
	private int currentIndex;

	/**
	 * Converts String data to char array.
	 * 
	 * @param data
	 */
	public QueryLexer(String data) {
		Objects.requireNonNull(data);
		this.data = data.toCharArray();
	}

	/**
	 * Returns last token generated by nextToken.
	 * 
	 * @return
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Generates new token.
	 * 
	 * @return token
	 */
	public Token nextToken() {
		if (token != null && token.getType() == TokenType.EOF) {
			throw new QueryLexerException();
		}
		if (data.length == 0 || currentIndex == data.length) {
			token = new Token(TokenType.EOF, null);
			return token;
		}
		skipWhiteSpaces();
		if (currentIndex == data.length) {
			token = new Token(TokenType.EOF, null);
			return token;
		}
		StringBuilder bob = new StringBuilder();

		// Tokenizes attribute name, logical operator and like operator.
		if (currentIndex < data.length && Character.isLetter(data[currentIndex])) {
			bob.append(data[currentIndex]);
			currentIndex++;
			while (currentIndex < data.length && Character.isLetter(data[currentIndex])) {
				bob.append(data[currentIndex]);
				currentIndex++;
			}
			String bobString = bob.toString();
			if (bobString.equalsIgnoreCase("LIKE")) {
				token = new Token(TokenType.OPERATOR, bobString);
				return token;
			} else if (bobString.equalsIgnoreCase("AND")) {
				token = new Token(TokenType.LOGICAL_OPERATOR, bobString);
				return token;
			}
			token = new Token(TokenType.ATTRIBUTE_NAME, bob.toString());
			return token;

			// Tokenizes all operators but LIKE
		} else if (currentIndex < data.length && (data[currentIndex] == '<' || data[currentIndex] == '>'
				|| data[currentIndex] == '!' || data[currentIndex] == '=')) {
			bob.append(data[currentIndex++]);
			if (currentIndex < data.length && data[currentIndex] == '=') {
				bob.append(data[currentIndex++]);
			}
			token = new Token(TokenType.OPERATOR, bob.toString());
			return token;

			// Tokenizes string literal
		} else if (currentIndex < data.length && data[currentIndex] == '"') {
			int wildcardCounter = 0;
			currentIndex++;
			while (currentIndex < data.length && data[currentIndex] != '"') {
				if (data[currentIndex] == '*') {
					if (wildcardCounter >= 1) {
						throw new QueryLexerException();
					}
					wildcardCounter++;
				}
				bob.append(data[currentIndex++]);
			}
			currentIndex++; // to skip ending " character
			token = new Token(TokenType.STRING_LITERAL, bob.toString());
			return token;
		} else {
			throw new QueryLexerException();
		}
	}

	/**
	 * Skips white spaces.
	 */
	private void skipWhiteSpaces() {
		while (currentIndex < data.length && (data[currentIndex] == '\r' || data[currentIndex] == '\n'
				|| data[currentIndex] == '\t' || data[currentIndex] == ' ')) {
			currentIndex++;
		}
	}
}
