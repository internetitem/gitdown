package com.internetitem.gitdown;

import org.parboiled.Rule;
import org.pegdown.Parser;
import org.pegdown.ast.SuperNode;
import org.pegdown.plugins.InlinePluginParser;

public class GitdownUnlabeledImageRule extends Parser implements InlinePluginParser {

	public GitdownUnlabeledImageRule() {
		super(Integer.valueOf(ALL), Long.valueOf(1000), DefaultParseRunnerProvider);
	}

	@Override
	public Rule[] inlinePluginRules() {
		return new Rule[] {
			NodeSequence(
				Character.valueOf('!'),
				MaybeEmptyLabel(),
				FirstOf(
					ExplicitLink(true),
					ReferenceLink(true)
				)
			)
		};
	}

    public Rule MaybeEmptyLabel() {
        return Sequence(
                Character.valueOf('['),
                Boolean.valueOf(push(new SuperNode())),
                ZeroOrMore(TestNot(Character.valueOf(']')), NonAutoLinkInline(), Boolean.valueOf(addAsChild())),
                Character.valueOf(']')
        );
    }

}
