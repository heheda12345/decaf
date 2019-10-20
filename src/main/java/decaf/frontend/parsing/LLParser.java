package decaf.frontend.parsing;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import decaf.driver.Config;
import decaf.driver.Phase;
import decaf.frontend.tree.Tree;
import decaf.lowlevel.log.IndentPrinter;
import decaf.printing.PrettyTree;

/**
 * The alternative parser phase.
 */
public class LLParser extends Phase<InputStream, Tree.TopLevel> {

    public LLParser(Config config) {
        super("parser-ll", config);
    }

    @Override
    public Tree.TopLevel transform(InputStream input) {
        var lexer = new decaf.frontend.parsing.DecafLexer<Parser>(new InputStreamReader(input));
        var parser = new Parser();
        lexer.setup(parser, this);
        parser.setup(lexer, this);
        parser.parse();
        return parser.tree;
    }

    @Override
    public void onSucceed(Tree.TopLevel tree) {
        if (config.target.equals(Config.Target.PA1_LL)) {
            var printer = new PrettyTree(new IndentPrinter(config.output));
            printer.pretty(tree);
            printer.flush();
        }
    }

    boolean isFirst; // Don't raise error of the same position

    private class Parser extends decaf.frontend.parsing.LLTable {
        @Override
        boolean parse() {
            isFirst = true;
            var sv = parseSymbol(start, new TreeSet<>());
            if (sv == null) {
                return false;
            }
            return true;
        }

        @Override
        public int tokenOf(int code) {
            return switch (code) {
                case Tokens.VOID -> VOID;
                case Tokens.BOOL -> BOOL;
                case Tokens.INT -> INT;
                case Tokens.STRING -> STRING;
                case Tokens.CLASS -> CLASS;
                case Tokens.NULL -> NULL;
                case Tokens.EXTENDS -> EXTENDS;
                case Tokens.THIS -> THIS;
                case Tokens.WHILE -> WHILE;
                case Tokens.FOR -> FOR;
                case Tokens.IF -> IF;
                case Tokens.ELSE -> ELSE;
                case Tokens.RETURN -> RETURN;
                case Tokens.BREAK -> BREAK;
                case Tokens.NEW -> NEW;
                case Tokens.PRINT -> PRINT;
                case Tokens.READ_INTEGER -> READ_INTEGER;
                case Tokens.READ_LINE -> READ_LINE;
                case Tokens.BOOL_LIT -> BOOL_LIT;
                case Tokens.INT_LIT -> INT_LIT;
                case Tokens.STRING_LIT -> STRING_LIT;
                case Tokens.IDENTIFIER -> IDENTIFIER;
                case Tokens.AND -> AND;
                case Tokens.OR -> OR;
                case Tokens.STATIC -> STATIC;
                case Tokens.INSTANCE_OF -> INSTANCE_OF;
                case Tokens.LESS_EQUAL -> LESS_EQUAL;
                case Tokens.GREATER_EQUAL -> GREATER_EQUAL;
                case Tokens.EQUAL -> EQUAL;
                case Tokens.NOT_EQUAL -> NOT_EQUAL;
                case Tokens.ABSTRACT -> ABSTRACT;
                case Tokens.VAR -> VAR;
                case Tokens.FUN -> FUN;
                case Tokens.ARROW -> ARROW;
                default -> code; // single-character, use their ASCII code!
            };
        }

        protected void raiseError() {
            if (isFirst) {
                yyerror("syntax error");
                isFirst = false;
            }
        }

        protected int myNextToken() {
            isFirst = true;
            return nextToken();
        }

        /**
         * Parse function for every non-terminal, with error recovery.
         * NOTE: the current implementation is buggy and may throw {@link NullPointerException}.
         * TODO: find a correct implementation for error recovery!
         * TODO: You are free to change the method body as you wish, but not the interface!
         *
         * @param symbol the non-terminal to be parsed
         * @return the parsed value of {@code symbol} if parsing succeeds, or else {@code null}.
         */
        private SemValue parseSymbol(int symbol, Set<Integer> follow) {
            var begin = beginSet(symbol);
            var end = new TreeSet<Integer>();
            end.addAll(follow);
            end.addAll(followSet(symbol));
            boolean err = false;
            // System.out.println("parsing: " + symbol + name(symbol) + "token " + token + name(token));
            // System.out.println("begin set: " + begin);
            // System.out.println("end set: " + end);
            if (!begin.contains(token)) {
                // System.out.println("error1");
                raiseError();
                err = true;
                while (true) {
                    if (begin.contains(token))
                        break;
                    if (end.contains(token))
                        return null;
                    token = myNextToken();
                    // System.out.println("skip & read " + name(token));
                    if (token == 0)
                        return null;
                }
            }
            var result = query(symbol, token);
            var actionId = result.getKey(); // get user-defined action

            var right = result.getValue(); // right-hand side of production
            var length = right.size();
            var params = new SemValue[length + 1];

            for (var i = 0; i < length; i++) { // parse right-hand side symbols one by one
                var term = right.get(i);
                params[i + 1] = isNonTerminal(term)
                        ? parseSymbol(term, end) // for non terminals: recursively parse it
                        : matchToken(term) // for terminals: match token
                ;
            }
            if (err)
                return null;
            for (int i = 0; i < length; i++)
                if (params[i + 1] == null)
                    return null;
            act(actionId, params); // do user-defined action
            return params[0];
        }

        /**
         * Match if the lookahead token is the same as the expected token.
         *
         * @param expected the expected token.
         * @return sem value
         */
        private SemValue matchToken(int expected) {
            // System.out.println("matching " + name(expected) + " " + name(token));
            SemValue self = semValue;
            if (token != expected) {
                // System.out.println("token error");
                raiseError();
                return null;
            }

            token = myNextToken();
            return self;
        }
    }
}