package com.craftmend.openaudiomc.spigot.modules.commands.command;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.commands.adapters.SpigotCommandSenderAdapter;
import com.craftmend.openaudiomc.generic.commands.helpers.CommandMiddewareExecutor;
import com.craftmend.openaudiomc.generic.commands.interfaces.CommandMiddleware;
import com.craftmend.openaudiomc.generic.commands.middleware.CatchCrashMiddleware;
import com.craftmend.openaudiomc.generic.commands.middleware.CatchLegalBindingMiddleware;
import com.craftmend.openaudiomc.generic.commands.middleware.CleanStateCheckMiddleware;
import com.craftmend.openaudiomc.generic.networking.packets.client.voice.PacketClientToggleMicrophone;
import com.craftmend.openaudiomc.generic.networking.payloads.client.voice.ClientVoiceChatToggleMicrophonePayload;
import com.craftmend.openaudiomc.generic.platform.Platform;
import com.craftmend.openaudiomc.generic.storage.enums.StorageKey;
import com.craftmend.openaudiomc.spigot.OpenAudioMcSpigot;
import com.craftmend.openaudiomc.spigot.modules.players.PlayerService;
import com.craftmend.openaudiomc.spigot.modules.players.objects.SpigotConnection;
import lombok.NoArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class MicMuteCommand implements CommandExecutor {

    private CommandMiddleware[] commandMiddleware = new CommandMiddleware[] {
            new CatchLegalBindingMiddleware(),
            new CatchCrashMiddleware(),
            new CleanStateCheckMiddleware()
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (CommandMiddewareExecutor.shouldBeCanceled(new SpigotCommandSenderAdapter(sender), null, commandMiddleware)) return true;

        if (sender instanceof Player) {
            SpigotConnection spigotConnection = OpenAudioMc.getService(PlayerService.class).getClient(((Player) sender).getUniqueId());

            if (!spigotConnection.getClientConnection().isConnectedToRtc()) {
                String message = Platform.translateColors(StorageKey.MESSAGE_VC_NOT_CONNECTED.getString());
                sender.sendMessage(message);
                return true;
            }

            spigotConnection.getClientConnection().sendPacket(new PacketClientToggleMicrophone(new ClientVoiceChatToggleMicrophonePayload()));
        } else {
            sender.sendMessage("This command can only be used by players");
        }
        return true;
    }

}
