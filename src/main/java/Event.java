import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Event extends ListenerAdapter {

    final static int answer_size = 3;
    private static int hit = 0;
    private static int blow = 0;

    Map<User, String> nowplay = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("k.start")) {
            start(event);
            return;
        }
        if (event.getMessage().getContentRaw().equals("k.answer"))
                event.getChannel().sendMessage(event.getMember().getUser().getName() + "さん / " + nowplay.get(event.getMember().getUser())).queue();

        if (!nowplay.containsKey(event.getMember().getUser())) return;
        if (!StringUtils.isNumeric(event.getMessage().getContentRaw())) return;
        if (event.getMessage().getContentRaw().length() != answer_size) return;

        hit = 0;
        blow = 0;

        int[] user_answer = change(event.getMessage().getContentRaw());
        int[] answer_list = change(nowplay.get(event.getMember().getUser()));

        for (int i = 0; i < answer_list.length; i++) {
            for (int j = 0; j < user_answer.length; j++) {
                if (answer_list[j] == user_answer[i]) {
                    if (j == i) {
                        hit++;
                    } else {
                        blow++;
                    }
                }
            }
        }

        if (hit == answer_size) {
            event.getChannel().sendMessage("正解です。" + event.getMember().getUser().getName() + " さんのゲームを終了します。").queue();
            nowplay.remove(event.getMember().getUser());
        } else {
            event.getChannel().sendMessage("hit: " + hit + " / blow: " + blow).queue();
        }

    }

    public void start(MessageReceivedEvent event) {
        if (nowplay.containsKey(event.getMember().getUser())) return;
        printDirection(event);

        nowplay.put(event.getMember().getUser(), makeRightAnswer());
    }

    public static void printDirection(MessageReceivedEvent event) {
        String direction = "**" + event.getMember().getUser().getName() +  "** さんがゲームを開始します。\n"
                + "これから0~9の"+answer_size+"つの数字を当ててもらいます。\n"
                + "同じ数字が使用されていることはありません。\n"
                + "数字と位置が合っていた場合、\"hit\"\n"
                + "数字が合っていて位置が間違っていた場合、\"blow\"とカウントされます。\n\n";

        event.getChannel().sendMessage(direction).queue();
    }

    public static String makeRightAnswer() {
        int[] answer = new int[answer_size];
        for(int i = 0; i < answer.length; i++) {
            answer[i] = (int)(Math.random()*10);
            for(int j = i-1; j >=0; j--) {
                if(answer[j] == answer[i]) {
                    answer[i] = (int)(Math.random()*10);
                }
            }
        }
        String strAnswer = "";
        for(int i=0; i<answer.length; i++){
            strAnswer = strAnswer + answer[i];
        }
        return strAnswer;
    }

    public int[] change(String changeStr) {
        String[] cstr = new String[changeStr.length()];

        for (int i = 0; i < changeStr.length(); i++) {
            String str = String.valueOf(changeStr.charAt(i));
            cstr[i] = str;
        }

        int[] cint = new int[changeStr.length()];
        int[] iarr = Arrays.stream(cstr).mapToInt(Integer::parseInt).toArray();

        for (int i = 0; i < iarr.length; i++) {
            cint[i] = iarr[i];
        }
        return cint;
    }


}
