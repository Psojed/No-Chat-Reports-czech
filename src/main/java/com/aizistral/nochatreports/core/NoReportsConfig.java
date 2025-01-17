package com.aizistral.nochatreports.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.nochatreports.NoChatReports;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.util.Tuple;

public class NoReportsConfig {
	private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("NoChatReports.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static NoReportsConfig INSTANCE;
	private boolean demandOnClient, demandOnServer, enableDebugLog, convertToGameMessage = true,
			showServerSafety = true, suppressMessageTrustIndicators = true,
			scrambleTimestamps = true, alwaysHideReportButton = false;
	private List<String> whitelistedServers;

	private static NoReportsConfig getInstance() {
		if (INSTANCE == null) {
			loadConfig();
		}

		return INSTANCE;
	}

	public static void loadConfig() {
		NoChatReports.LOGGER.info(INSTANCE == null ? "Loading config..." : "Reloading config...");

		INSTANCE = readFile();

		if (INSTANCE == null) {
			INSTANCE = new NoReportsConfig();
		}

		if (INSTANCE.whitelistedServers == null) {
			INSTANCE.whitelistedServers = new ArrayList<>();
		}

		writeFile(INSTANCE);
	}

	public static void saveConfig() {
		if (INSTANCE != null) {
			writeFile(INSTANCE);
		}
	}

	@Nullable
	private static NoReportsConfig readFile() {
		if (!Files.isRegularFile(CONFIG_FILE))
			return null;

		try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE)) {
			return GSON.fromJson(reader, NoReportsConfig.class);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void writeFile(NoReportsConfig instance) {
		try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
			GSON.toJson(instance, writer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static boolean demandsOnClient() {
		return getInstance().demandOnClient;
	}

	public static boolean demandsOnServer() {
		return getInstance().demandOnServer;
	}

	public static boolean convertToGameMessage() {
		return getInstance().convertToGameMessage;
	}

	public static boolean showServerSafety() {
		return getInstance().showServerSafety;
	}

	public static List<String> getWhitelistedServers() {
		return getInstance().whitelistedServers;
	}

	public static boolean isWhitelistedServer(ServerAddress address) {
		return getWhitelistedServers().contains(address.getHost() + ":" + address.getPort());
	}

	public static boolean suppressMessageTrustIndicators() {
		return getInstance().suppressMessageTrustIndicators;
	}

	public static boolean isDebugLogEnabled() {
		return getInstance().enableDebugLog;
	}

	public static boolean scrambleTimestamps() {
		return getInstance().scrambleTimestamps;
	}

	public static boolean alwaysHideReportButton() {
		return getInstance().alwaysHideReportButton;
	}

}
