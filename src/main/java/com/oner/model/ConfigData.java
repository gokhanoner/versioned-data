package com.oner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ConfigData implements Serializable {
    private String data;
    private ZonedDateTime effectiveSince;
}
