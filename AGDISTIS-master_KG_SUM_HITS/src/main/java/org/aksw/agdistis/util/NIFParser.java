/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.agdistis.util;

import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author diegomoussallem
 */
public class NIFParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(NIFParser.class);
	protected JSONParser jsonParser = new JSONParser();

	public String createTextWithMentions(String text, List<Span> mentions) {
		// Example: 'The <entity>University of Leipzig</entity> in
		// <entity>Barack Obama</entity>.'

		Collections.sort(mentions, new StartPosBasedComparator());

		StringBuilder textBuilder = new StringBuilder();
		int lastPos = 0;
		for (int i = 0; i < mentions.size(); i++) {
			Span span = mentions.get(i);

			int begin = span.getStartPosition();
			int end = begin + span.getLength();

			if (begin < lastPos) {
				// we have two overlapping mentions --> take the larger one
				Span prev = mentions.get(i - 1);
				LOGGER.warn("\"{}\" at pos {} overlaps with \"{}\" at pos {}",
						text.substring(span.getStartPosition(), span.getStartPosition() + span.getLength()),
						span.getStartPosition(),
						text.substring(prev.getStartPosition(), prev.getStartPosition() + prev.getLength()),
						prev.getStartPosition());
				if (span.getLength() > prev.getLength()) {
					// current is larger --> replace previous with current
					textBuilder.delete(textBuilder.length() - prev.getLength(), textBuilder.length());
					lastPos -= prev.getLength();
				} else // previous is larger or equal --> skip current
				{
					continue;
				}
			}
			String before = text.substring(lastPos, begin);
			String label = text.substring(begin, end);
			lastPos = end;
			textBuilder.append(before).append("<entity>" + label + "</entity>");
		}

		String lastSnippet = text.substring(lastPos, text.length());
		textBuilder.append(lastSnippet);

		return textBuilder.toString();
	}
}
