package com.allNews.managers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allNews.activity.Preferences;
import com.allNews.application.App;
import com.allNews.data.NewApp.NewApp;
import com.allNews.data.News;
import com.allNews.utils.Utils;
import com.allNews.web.Statistic;
import com.android.volley.RequestQueue;

import gregory.network.rss.R;

public class DialogManager {
    static String msg;

    public static void eventCreateDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.event_create_msg))
                .setPositiveButton(context.getString(R.string.to_web),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent browserIntent = new Intent(
                                        Intent.ACTION_VIEW, Uri
                                        .parse("http://2Event.com"));

                                context.startActivity(browserIntent);

                            }

                        })
                .setNegativeButton(context.getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }

                        }).show();

    }

    public static void eventAboutDialog(final Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String lang = sp.getString(Preferences.PREF_LANGUAGE,
                Preferences.LANGUAGE_RU);

        msg = context.getString(R.string.events_title);
        if (lang.equals(Preferences.LANGUAGE_RU))
            msg = msg
                    + "<br>Кроме событий созданных на нашем сайте, мы собираем и отбираем афишу событий с десятка сайтов. Таких как Eventbrite, Timepad, Concert.ua..."
                    + "<br>"
                    + "<br>Как разместить свое мероприятие в этом списке?"
                    + "<br>Просто создать его на сайте 2Event.com"
                    + "<br>"
                    + "<br>Про 2Event.com"
                    + "<br>Это бесплатная платформа для организации мероприятий."
                    + "<br>&nbsp;&nbsp;&nbsp;• Самообслуживание. Создайте свое мероприятие за 10 минут."
                    + "<br>&nbsp;&nbsp;&nbsp;• Продажа билетов или просто регистрация посетителей."
                    + "<br>&nbsp;&nbsp;&nbsp;• Печать бейджей и билетов."
                    + "<br>&nbsp;&nbsp;&nbsp;• Бесплатный сайт для мероприятия."
                    + "<br>&nbsp;&nbsp;&nbsp;• Бесплатное мобильное приложение для мероприятия. <a href=\"http://2event.com/gplay\">Android</a>&nbsp;&nbsp;&nbsp; <a href=\"http://2event.com/itunes\">IOS</a>"
                    + "<br>&nbsp;&nbsp;&nbsp;• На сайте и в приложении: Расписание выступлений. Вопросы докладчикам. Список посетителей. Назначение встреч. Чат."
                    + "<br>&nbsp;&nbsp;&nbsp;• <a href=\"http://2event.com/events/comments/101281/\">Чат для проектора.</a>Подтягиваем по хештегу сообщения из Твиттера. А также, каждых 30-ть секунд на экран выводится случайный посетитель."
                    + "<br>&nbsp;&nbsp;&nbsp;• Подбор попутчиков на мероприятие. Чекины и билеты на поезд или самолет."
                    + "<br>&nbsp;&nbsp;&nbsp;• Подбор отелей, где селятся другие посетители мероприятия. Бронь отелей.";
        else if (lang.equals(Preferences.LANGUAGE_EN))
            msg = msg
                    + "<br>Besides the events, which was created on our website, we gather and select the affishe of the events from dozen of websites. Such as Eventbride, Timepad, Concert.uaпїЅ"
                    + "<br>"
                    + "<br>How to place your event on this list?"
                    + "<br>Just create it on the www.2Event.com website"
                    + "<br>"
                    + "<br>About 2Event.com"
                    + "<br>It is a free platform for event organizing."
                    + "<br>&nbsp;&nbsp;&nbsp;• Self-service. Create your event in 10 minutes."
                    + "<br>&nbsp;&nbsp;&nbsp;• Ticket selling or just visitors registration."
                    + "<br>&nbsp;&nbsp;&nbsp;• Tickets and badges printing."
                    + "<br>&nbsp;&nbsp;&nbsp;• Free website for  event. "
                    + "<br>&nbsp;&nbsp;&nbsp;• Free mobile app for event. <a href=\"http://2event.com/gplay\">Android</a>&nbsp;&nbsp;&nbsp; <a href=\"http://2event.com/itunes\">IOS</a>"
                    + "<br>&nbsp;&nbsp;&nbsp;• On the website and in mobile app: Speakers schedule. Questions to speakers. List of attendees. Appointments. Chat."
                    + "<br>&nbsp;&nbsp;&nbsp;• <a href=\"http://2event.com/events/comments/101281/\">Chat for projector.</a> Pulling up Twitter messages with the help of hashtags. And also every 30 second dispayed random visitor."
                    + "<br>&nbsp;&nbsp;&nbsp;• Pick up fellow travellers to event. Check in, train, avia tickets."
                    + "<br>&nbsp;&nbsp;&nbsp;• Hotels selection, where the other event attendees are living. Hotel booking.";
        else
            msg = msg
                    + "<br>Крім подій створених на нашому сайті, ми підбираємо афішу подій з десятка сайтів. Таких як Eventbrite, Timepad, Concert.ua ..."
                    + "<br>"
                    + "<br>Як розмістити свій захід в цьому списку?"
                    + "<br>Просто створити його на сайті 2Event.com"
                    + "<br>"
                    + "<br>Про 2Event.com"
                    + "<br>Це безкоштовна платформа для організації заходів."
                    + "<br>&nbsp;&nbsp;&nbsp;• Самообслуговування. Створіть свій захід за 10 хвилин."
                    + "<br>&nbsp;&nbsp;&nbsp;• Продаж квитків або просто реєстрація відвідувачів."
                    + "<br>&nbsp;&nbsp;&nbsp;• Друк бейджів і квитків."
                    + "<br>&nbsp;&nbsp;&nbsp;• Безкоштовний сайт для заходу."
                    + "<br>&nbsp;&nbsp;&nbsp;• Безкоштовне мобільний додаток для заходу. <a href=\"http://2event.com/gplay\">Android</a>&nbsp;&nbsp;&nbsp; <a href=\"http://2event.com/itunes\">IOS</a>"
                    + "<br>&nbsp;&nbsp;&nbsp;• На сайті і в додатку: Розклад виступів. Питання доповідачам. Список відвідувачів. Призначення зустрічей. Чат."
                    + "<br>&nbsp;&nbsp;&nbsp;• <a href=\"http://2event.com/events/comments/101281/\">Чат для проектора.</a>Підтягуємо за хештегом повідомлення з Твіттера. А також, кожних 30-ть секунд на екран виводиться випадковий посетітель."
                    + "<br>&nbsp;&nbsp;&nbsp;• Підбір попутників на захід. Чекин і квитки на поїзд чи літак."
                    + "<br>&nbsp;&nbsp;&nbsp;• Підбір готелів, де селяться інші відвідувачі заходу. Бронь готелів.";

        final TextView message = new TextView(context);

        message.setText(Html.fromHtml(msg));
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setPadding(10, 10, 10, 10);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(message);
        builder.setNegativeButton(context.getString(R.string.close), null);
        builder.setPositiveButton(context.getString(R.string.event_share), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT,
                                context.getString(R.string.share_event_help_msg));
                        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                Html.fromHtml(msg));
                        try {
                            Statistic.sendStatistic(context,
                                    Statistic.CATEGORY_CLICK,
                                    Statistic.ACTION_CLICK_BTN_SHARE,
                                    "event share", 0L);

                            context.startActivity(Intent
                                    .createChooser(
                                            sendIntent,
                                            context.getResources().getString(
                                                    R.string.app_name)
                                                    + ":"));
                        } catch (android.content.ActivityNotFoundException ignored) {
                        }

                        dialog.dismiss();
                    }
                });

        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                Drawable drawable = context.getResources().getDrawable(
                        R.drawable.ic_share);

                if (drawable != null) {
                    drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5),
                            0, (int) (drawable.getIntrinsicWidth() * 1.5),
                            drawable.getIntrinsicHeight());
                }
                button.setCompoundDrawables(drawable, null, null, null);
            }
        });

    }

    public static void showPasswordDialog(final Context context) {
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final Animation shake = AnimationUtils.loadAnimation(context,
                R.anim.shake);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources()
                .getString(R.string.input_pass));
        builder.setView(input);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Utils.showVirtualKeyBoard(input.findFocus(), (Activity) context);
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // int value = input.getText().toString().hashCode();
                        String value = input.getText().toString();
                        if (value.equals(Preferences.PASSWORD_FOR_READ_ALL_NEWS)) {
                            //	saveAdminPref(context);
                            MyPreferenceManager.savePassToReadAllNewsPref(context);
                            dialog.dismiss();
                        } else {
                            input.startAnimation(shake);
                        }
                    }
                });

    }

    public static void showAdminDialog(final Context context) {
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final Animation shake = AnimationUtils.loadAnimation(context,
                R.anim.shake);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources()
                .getString(R.string.input_pass));
        builder.setView(input);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Utils.showVirtualKeyBoard(input.findFocus(), (Activity) context);
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int value = input.getText().toString().hashCode();
                        if (value == Preferences.PASSWORD_FOR_PUSH)
                            saveAdminPref(context);

                        dialog.dismiss();
                    }
                });

    }
    public static void showPromoCodeDialog(final Context context){
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final Animation shake = AnimationUtils.loadAnimation(context,
                R.anim.abc_grow_fade_in_from_bottom);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.promo_code);
        builder.setView(input);
     builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Utils.showVirtualKeyBoard(input.findFocus(), (Activity) context);
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int value = input.getText().toString().hashCode();
                        if (value == Preferences.PASSWORD_FOR_PROMO) {
                            savePromoPref(context);
                            Toast.makeText(context,"Promo Code is OK", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, "Promo Code Incorrect", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });

    }

    private static void savePromoPref(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
             SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Preferences.PREF_PROMO, true);
        editor.apply();
    }

    protected static void saveAdminPref(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean adminMode = sp.getBoolean(Preferences.PREF_ADMIN, false);

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Preferences.PREF_ADMIN, !adminMode);
        editor.apply();

    }

    public static void showDeleteDbDialog(final Context context) {

        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.pref_clean_title)
                .setMessage(context.getString(R.string.db_delete))
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                ManagerNews.tryClearDb(context);

                            }

                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }

                        }).show();
    }

    public static void showSendPushDialog(final Context context,
                                          final int newsId) {

        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Відправити пуш")
                .setMessage("Відправити пуш?")
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                RequestQueue requestQueue = App.getRequestQueue();
                                requestQueue.add(ManagerApp.sendPush(context,
                                        newsId));

                            }

                        }).setNegativeButton(R.string.no, null).show();
    }

    public static void showNoMemmoryDialog(final Context context) {

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.no_memmory_title)
                .setMessage(context.getString(R.string.no_memmory_msg))
                .setPositiveButton(R.string.close,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                ((Activity) context).finish();

                            }

                        }).show();
    }

    public static void openDialogShareUS(Context context, String label) {
        String urls = context.getResources().getString(R.string.app_url)
                + "\n\n"
                + context.getResources().getString(R.string.app_iphone_url);
        String appName = context.getResources().getString(R.string.app_name);
        String appFullName = context.getResources().getString(
                R.string.app_full_name);
        String text = context.getResources().getString(R.string.share_app_msg,
                appFullName)
                + "\n\n" + urls;

        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, appFullName);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        try {
            Statistic.sendStatistic(context, Statistic.CATEGORY_CLICK,
                    Statistic.ACTION_CLICK_BTN_SHARE, label + "  " + appName,
                    0L);

            context.startActivity(Intent.createChooser(sendIntent, appName
                    + ":"));
        } catch (android.content.ActivityNotFoundException ignored) {
        }

    }

    public static void openDialogShareNews(Context context, News currentNews,
                                           boolean onlyGmail) {

        String app_name = context.getResources().getString(R.string.app_name);

        String subject = currentNews.getTitle() + context.getResources().getString(R.string.facebook_share_title);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        if (onlyGmail) {
            sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
                    + ""));
        }

        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                getTextToShareNews(context, currentNews));



        try {
            Statistic
                    .sendStatistic(context, Statistic.CATEGORY_CLICK,
                            Statistic.ACTION_CLICK_BTN_SHARE,
                            Statistic.LABEL_SHARE, 0L);
            if (!onlyGmail)
                context.startActivity(Intent.createChooser(sendIntent, app_name
                        + ":"));
            else
                context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ignored) {
        }

    }

    public static void openDialogShareNewApp(Context context, NewApp currentNews,
                                           boolean onlyGmail) {

        String app_name = context.getResources().getString(R.string.app_name);

        String subject = currentNews.getTitle();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        if (onlyGmail) {
            sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
                    + ""));
        }


        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                getTextToShareNewApp(context, currentNews));

        try {
            Statistic
                    .sendStatistic(context, Statistic.CATEGORY_CLICK,
                            Statistic.ACTION_CLICK_BTN_SHARE,
                            Statistic.LABEL_SHARE, 0L);
            if (!onlyGmail)
                context.startActivity(Intent.createChooser(sendIntent, app_name
                        + ":"));
            else
                context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ignored) {
        }

    }
       public static String getTextToShareNews(Context context, News currentNews) {
        String app_name = context.getResources().getString(R.string.app_name);
        String link_val = context.getResources().getString(R.string.app_url);
        String link_iphone = context.getResources().getString(
                R.string.app_iphone_url);
        String trimFullText = currentNews.getcontent();
        // if (currentNews.isNewApp() == 0)
        trimFullText = Html.fromHtml(trimFullText).toString();

        if (currentNews.isNewApp() == 0 && trimFullText.length() >= 100) {
            int dotIndex = trimFullText.indexOf(". ", 100) + 1;
            if (dotIndex < 100)
                dotIndex = 100;

            trimFullText = trimFullText.substring(0, dotIndex);
        }
        trimFullText = context.getResources()
                .getString(R.string.share_news_msg)
                + "\n\n"
                + currentNews.getTitle() + "\n\n" + trimFullText;

        return currentNews.getLink()  + "\n\n" + trimFullText  + "\n\n  "
                + "-----------------------------------\n\n"
                + context.getResources().getString(R.string.share) + " "
                + app_name + "\n\n  " + link_val + "\n\n  " + link_iphone;
    }
    public static String getTextToShareNewApp(Context context, NewApp currentNews) {
        String app_name = context.getResources().getString(R.string.app_name);
        String link_val = context.getResources().getString(R.string.app_url);
        String link_iphone = context.getResources().getString(
                R.string.app_iphone_url);
        String trimFullText = currentNews.getContent();
        // if (currentNews.isNewApp() == 0)
        trimFullText = Html.fromHtml(trimFullText).toString();

        if ( trimFullText.length() >= 100) {
            int dotIndex = trimFullText.indexOf(". ", 100) + 1;
            if (dotIndex < 100)
                dotIndex = 100;

            trimFullText = trimFullText.substring(0, dotIndex);
        }
        trimFullText = context.getResources()
                .getString(R.string.share_news_msg)
                + "\n\n"
                + currentNews.getTitle() + "\n\n" + trimFullText;

        return trimFullText + "\n\n" + currentNews.getRefLink() + "\n\n  "
                + "-----------------------------------\n\n"
                + context.getResources().getString(R.string.share) + " "
                + app_name + "\n\n  " + link_val + "\n\n  " + link_iphone;
    }


    public static void openDialogFeedBack(Context context) {
        try {
            Intent emailIntent;
            if (App.getContext().getResources().getBoolean(R.bool.sport_news)){
                emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "onpress.info@gmail.com",
                                null));
            }else if (App.getContext().getResources().getBoolean(R.bool.need_event)){
                emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "novostin7@mail.ru",
                                null));
            }
            else {
                emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "yaroslav.maxymovych@gmail.com",
                                null));
            }

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources()
                    .getString(R.string.pref_feedback_title));

            Statistic.sendStatistic(context, Statistic.CATEGORY_CLICK,
                    Statistic.ACTION_CLICK_BTN_FEEDBACK, "", 0L);

            context.startActivity(Intent.createChooser(emailIntent,
                    "Send email..."));
        } catch (android.content.ActivityNotFoundException ignored) {
        }

    }
}
