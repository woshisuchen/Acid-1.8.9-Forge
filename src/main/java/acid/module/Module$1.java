package acid.module;

import acid.api.value.Mode;
import acid.api.value.Numbers;
import acid.api.value.Option;
import acid.api.value.Value;
import acid.command.Command;
import acid.module.Module;
import acid.utils.Helper;
import acid.utils.math.MathUtil;

class Module$1
extends Command {
    private final Module m;
    final Module this$0;

    Module$1(Module var1, String $anonymous0, String[] $anonymous1, String $anonymous2, String $anonymous3) {
        super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        this.this$0 = var1;
        this.m = var1;
    }

    public String execute(String[] args) {
        if (args.length >= 2) {
            Option option = null;
            Numbers fuck = null;
            Mode xd = null;
            for (Value v : this.m.values) {
                if (!(v instanceof Option) || !v.getName().equalsIgnoreCase(args[0])) continue;
                option = (Option)v;
            }
            if (option != null) {
                option.setValue((Object)((Boolean)option.getValue() == false ? 1 : 0));
                Helper.sendMessage((String)String.format((String)"> %s has been set to %s", (Object[])new Object[]{option.getName(), option.getValue()}));
            } else {
                for (Value v : this.m.values) {
                    if (!(v instanceof Numbers) || !v.getName().equalsIgnoreCase(args[0])) continue;
                    fuck = (Numbers)v;
                }
                if (fuck != null) {
                    if (MathUtil.parsable((String)args[1], (byte)4)) {
                        double v1 = MathUtil.round((double)Double.parseDouble((String)args[1]), (int)1);
                        fuck.setValue((Object)(v1 > (Double)fuck.getMaximum() ? (Double)fuck.getMaximum() : v1));
                        Helper.sendMessage((String)String.format((String)"> %s has been set to %s", (Object[])new Object[]{fuck.getName(), fuck.getValue()}));
                    } else {
                        Helper.sendMessage((String)("> " + args[1] + " is not a number"));
                    }
                }
                for (Value v : this.m.values) {
                    if (!args[0].equalsIgnoreCase(v.getDisplayName()) || !(v instanceof Mode)) continue;
                    xd = (Mode)v;
                }
                if (xd != null) {
                    if (xd.isValid(args[1])) {
                        xd.setMode(args[1]);
                        Helper.sendMessage((String)String.format((String)"> %s set to %s", (Object[])new Object[]{xd.getName(), xd.getModeAsString()}));
                    } else {
                        Helper.sendMessage((String)("> " + args[1] + " is an invalid mode"));
                    }
                }
            }
            if (fuck == null && option == null && xd == null) {
                this.syntaxError("Valid .<module> <setting> <mode if needed>");
            }
        } else if (args.length >= 1) {
            Option option = null;
            for (Value fuck1 : this.m.values) {
                if (!(fuck1 instanceof Option) || !fuck1.getName().equalsIgnoreCase(args[0])) continue;
                option = (Option)fuck1;
            }
            if (option != null) {
                option.setValue((Object)((Boolean)option.getValue() == false ? 1 : 0));
                String fuck2 = option.getName().substring(1);
                String xd2 = option.getName().substring(0, 1).toUpperCase();
                if (((Boolean)option.getValue()).booleanValue()) {
                    Helper.sendMessage((String)String.format((String)"> %s has been set to \u00a7a%s", (Object[])new Object[]{xd2 + fuck2, option.getValue()}));
                } else {
                    Helper.sendMessage((String)String.format((String)"> %s has been set to \u00a7c%s", (Object[])new Object[]{xd2 + fuck2, option.getValue()}));
                }
            } else {
                this.syntaxError("Valid .<module> <setting> <mode if needed>");
            }
        } else {
            Helper.sendMessage((String)String.format((String)"%s Values: \n %s", (Object[])new Object[]{this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1).toLowerCase(), this.getSyntax(), "false"}));
        }
        return null;
    }
}
