package net.zetaeta.settlement.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a LocalCommandExecutor that can have subcommands added to it. While all subcommands have the {@link net.zetaeta.settlement.commands.SettlementCommand#registerSubCommand(net.zetaeta.libraries.commands.local.LocalCommandExecutor)}
 * method, only some actually check for these, and these should be annotated with this annotation.
 * @author Daniel
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface SubCommandable {
    
}
