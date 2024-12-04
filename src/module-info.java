/**
 * 
 */
/**
 * The module for this assignment.   Please change the module name below to match the project name.
 * @author swong
 *
 */
module comp310_f21_finalproject_comp310_f21_finalproject_21 {
	requires transitive java.desktop;
	requires transitive java.rmi;
	requires java.base;
	requires transitive com.google.gson;
	requires jcef;
	
	exports provided.logger;
	exports provided.config;
	exports provided.datapacket;
	exports provided.datapacket.test;
	exports provided.discovery;
	exports provided.discovery.impl;
	exports provided.discovery.impl.model;
	exports provided.discovery.impl.view;
	exports provided.extvisitor;
	exports provided.mixedData;
	exports provided.mixedData.test;
	exports provided.owlMaps;
	exports provided.owlMaps.cefUtils;
	exports provided.owlMaps.cefUtils.gson;
	exports provided.owlMaps.cefUtils.impl;
	exports provided.owlMaps.cefUtils.wrapper;
	exports provided.owlMaps.components;
	exports provided.owlMaps.components.infowindow;
	exports provided.owlMaps.components.marker;
	exports provided.owlMaps.components.overlay;
	exports provided.owlMaps.components.shapes;
	exports provided.owlMaps.control;
	exports provided.owlMaps.control.impl;
	exports provided.owlMaps.demo;
	exports provided.owlMaps.demo.controller;
	exports provided.owlMaps.demo.model;
	exports provided.owlMaps.demo.view;
	exports provided.owlMaps.general;
	exports provided.owlMaps.general.impl;
	exports provided.owlMaps.map;
	exports provided.owlMaps.map.data;
	exports provided.owlMaps.mouse;
	exports provided.owlMaps.utils;
	exports provided.owlMaps.utils.impl;
	exports provided.pubsubsync;
	exports provided.pubsubsync.impl;
	exports provided.rmiUtils;
	exports provided.rmiUtils.classServer;
	exports provided.utils;
	exports provided.utils.dispatcher;
	exports provided.utils.dispatcher.impl;
	exports provided.utils.displayModel;
	exports provided.utils.file;
	exports provided.utils.file.impl;
	exports provided.utils.function;
	exports provided.utils.loader;
	exports provided.utils.loader.impl;
	exports provided.utils.logic;
	exports provided.utils.logic.impl;
	exports provided.utils.struct;
	exports provided.utils.struct.impl;
	exports provided.utils.valueGenerator;
	exports provided.utils.valueGenerator.impl;
	
	opens provided.owlMaps.cefUtils.impl;

	
	/**
	 *  Add exports for at least the following package and necessary sub-packages: 
	 *   - common
	 *   - student-defined message types and implementations
	 *   - any serialized support packages used by message implementations
	 */
	exports common.connector;
	exports common.receiver;
	exports common.connector.messages;
	exports common.receiver.messages;
	exports common.adapter;
	exports yh87_cp46.chatApp.model.message;
	exports yh87_cp46.chatRoom.miniModel.message;
	exports yh87_cp46.chatApp.model;

	
}