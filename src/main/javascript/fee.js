/**
 * /Users/rbe/project/IRB/fee/web-app/js/fee.js
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Fee - Frontend Engine: jQuery-based JavaScript library.
 *
 */

FEE$ = function(opts, selector) {
	
	// Default settings
	this.defaults = {
		// Trace?
		trace: false,
		// Debug?
		debug: false,
		// URL of Glue server sending and receiving JSON data
		url: "http://localhost:8080/glue/fee",
		// Synchronization of changes with Glue server
		commit: {
			type: 'auto', // Default is to auto-commit every single change
			eventTrigger: ['fee-commit'], // List of events that trigger a sync
			keyCodeTrigger: [ // List of key codes that trigger a sync
					9, // Tab
					13, // Return
				],
			interval: 5, // Commit interval; synchronize changes at least every x seconds
		},
		rollback: {
			eventTrigger: ['fee-rollback'], // List of events that trigger a sync
			keyCodeTrigger: [ // List of key codes that trigger a rollback
					27, // Escape
				], 
		}
	}
	
	// Save selector used by client/jQuery
	this.selector = selector
	// Set a well-known reference to jQuery
	this.$ = jQuery
	// Save jQuery'd version of selector
	this.$$ = $(selector)
	// Save ID
	this.id = this.$$.attr('id')
	// Merge given options with defaults
	this.opts = $.extend(true, this.defaults, opts)
	// The old and new value
	this.lastValue = this.$$.val() // Initialize with actual value
	
	/**
	 * Print a trace message to the console.
	 */
	this.t = function(msg) {
		if (this.opts.trace) {
			FEE.d(this.id, msg)
		}
	}
	
	/**
	 * Print a debug message to the console.
	 */
	this.d = function(msg) {
		if (this.opts.debug) {
			FEE.d(this.id, msg)
		}
	}
	
	/**
	 * Print an error message to the console.
	 */
	this.w = function(msg) {
		if (this.opts.debug) {
			FEE.w(this.id, msg)
		}
	}
	
	/**
	 * Print an error message to the console.
	 */
	this.e = function(msg) {
		if (this.opts.debug) {
			FEE.e(this.id, msg)
		}
	}
	
	/**
	 * Dump information of an object.
	 */
	this.dump = function(t) {
		this.d("dump: " + t.attr('id') + " type: " + (typeof t))
		var context = t.context
		this.d("....: context.nodeName=" + context.nodeName + " index=" + t.index(this))
		this.d(t)
		// Allow chaining
		return this
	}
	
	/**
	 * Was this value modified?
	 */
	this.dirty = function() {
		var curValue = this.$$.val()
		this.t("dirty: old='" + this.lastValue + "' == curValue='" + curValue + "'?")
		return this.lastValue != curValue
	}
	
	/**
	 * Communicate with Glue server: commit changes made to this element to Glue server.
	 */
	this.commit = function() {
		if (this.dirty()) {
			this.d("comitting " + this.id + " = " + this.$$.val())
			// Execute pre-callback
			FEE.callFn(this.opts.preCommit, this.$$)
			// Save actual value
			this.lastValue = this.$$.val()
			// Execute after-callback
			FEE.callFn(this.opts.afterCommit, this.$$)
		} else {
			this.d("no commit needed")
		}
		// Allow chaining
		return this
	}
	
	/**
	 * Communicate with Glue server: rollback changes made to this element.
	 */
	this.rollback = function() {
		if (this.dirty()) {
			this.d("rolling back " + this.id + " = " + this.$$.val() + ", was '" + this.lastValue + "' before")
			// Execute pre-callback
			FEE.callFn(this.opts.preRollback, this.$$)
			// Set old value
			this.$$.val(this.lastValue)
			// Execute after-callback
			FEE.callFn(this.opts.afterRollback, this.$$)
		}
		// Allow chaining
		return this
	}
	
	/**
	 * Keep value in sync with Glue server.
	 * If we changed the value, send it to Glue
	 *    otherwise check for changes at the Glue side.
	 */
	this.sync = function() {
		
	}
	
}

/**
 * Fee
 */
FEE = {
	
	$: jQuery,
	
	// Cached view definitions
	views: {
		// Example, a view named 'ralf'
		ralf: [ // A list of elements
			{
				id: '', // Name of this element
				widget: 'textfield', // How to render this element?
			}
		]
	},
	
	/** Log Trace */
	t: function(id, msg) {
		if (typeof(msg) == 'object') {
			window.console.debug(msg)
		} else {
			window.console.debug("TRACE   FEE/#" + id + ": " + msg)
		}
	},
	
	/** Log Debug */
	d: function(id, msg) {
		if (typeof(msg) == 'object') {
			window.console.debug(msg)
		} else {
			window.console.debug("DEBUG   FEE/#" + id + ": " + msg)
		}
	},
	
	/** Log Warning */
	w: function(id, msg) {
		if (typeof(msg) == 'object') {
			window.console.debug(msg)
		} else {
			window.console.debug("WARNING FEE/#" + id + ": " + msg)
		}
	},
	
	/** Log Error */
	e: function(id, msg) {
		if (typeof(msg) == 'object') {
			window.console.debug(msg)
		} else {
			window.console.debug("ERROR   FEE/#" + id + ": " + msg)
		}
	},
	
	/**
	 * Create and return a "version 4" RFC-4122 UUID string.
	 * See http://ajaxian.com/archives/uuid-generator-in-javascript
	 */
	randomUUID: function() {
		var s = [], itoh = '0123456789ABCDEF'
		// Make array of random hex digits. The UUID only has 32 digits in it, but we
		// allocate an extra items to make room for the '-'s we'll be inserting.
		for (var i = 0; i < 36; i++) s[i] = Math.floor(Math.random() * 0x10)
		// Conform to RFC-4122, section 4.4
		s[14] = 4 // Set 4 high bits of time_high field to version
		s[19] = (s[19] & 0x3) | 0x8 // Specify 2 high bits of clock sequence
		// Convert to hex chars
		for (var i = 0; i <36; i++) s[i] = itoh[s[i]]
		// Insert '-'s
		s[8] = s[13] = s[18] = s[23] = '-'
		return s.join('')
	},
	
	/**
	 * Call a function 'if it exists'.
	 * @param fn Function; can be null.
	 */
	callFn: function(fn, params) {
		this.t(null, "calling function=" + fn + " with params=" + params + " array=" + (params instanceof Array))
		if (!(params instanceof Array)) {
			params = [params]
		}
		if (fn) {
			fn.apply(this, params)
		}
	},
	
	/**
	 * Dump contents of a form.
	 */
	dumpForm: function(formId) {
		var str = ""
		this.$('#' + formId).map(function() {
			var id = this.$(this).attr("id")
			var val = this.$(this).val()
			str.append(id + " = " + val)
		})
		this.d(str)
	},
	
	/**
	 * Get view definition from Glue server.
	 */
	fetchView: function(viewName) {
	},
	
	/**
	 * Render view definition.
	 */
	renderView: function(viewName, divName) {
		var rowDiv = this.$(document).createElement("div")
		var labelDiv = this.$(document).createElement("div")
		var valueDiv = this.$(document).createElement("div")
	},
	
	/**
	 * UI - several helpers for dealing with elements.
	 */
	ui: {
		
		/**
		 * Request attention for a certain element.
		 */
		attention: function(t) {
			t.addClass("feeAttention").focus()
			// Allow chaining
			return t
		},
		
		/**
		 * Copy - clone an element.
		 */
		copy: function(id, newid, append) {
			FEE.d("FEE.ui.copy: id=" + id + " newid=" + newid + " append=" + append)
			// Make copy of element
			var cl = this.$(id).clone()
			// Set new id TODO Check if newid already exists
			cl.attr("id", newid)
			// Append cloned/new element
			this.$(append).append(cl)
			// Allow chaining
			return this.$(newid);
		},
		
		/**
		 * Fade in - show an element.
		 */
		fadeIn: function(t) {
			t.f$.d("fadeIn: " + t.attr('id'))
			t.show("fast")
			// Allow chaining
			return t
		},
		
		/**
		 * Fade out - hide an element.
		 */
		fadeOut: function(t) {
			t.f$.d("fadeOut: " + t.attr('id'))
			t.hide("fast")
			// Allow chaining
			return t;
		},
		
		/**
		 * Mark occurences of a string.
		 */
		markOccurence: function(str, cssdef) {
			FEE.$(":contains(" + str + ")").css(cssdef)
		},
		
		/**
		 * Make an element editable.
		 */
		makeEditable: function(t) {
			t.hover(function() { // Show user that field's value can be edited
					t.toggleClass('feeHighlightEditable')
				})
				.click(function(e) { // Start inline editing
					// We're already in editing mode, do nothing
					if (t.hasClass('feeActive')) return
					// Save HTML content
					var content = this.$.trim(t.html())
					// Make element empty; remove content from element
					t.addClass("feeActive").empty()
					// Determine type of editable
					var element = '<input type="text" class="feeTextField"></input>'
					this.$(element)
						.val(content)
						.appendTo(t)
						.focus()
						.blur(function(e) {
							t.trigger('blur')
						})
				})
				.blur(function(e) { // End the inline editing
					// Find first element providing a value
					var contents = t.find(':first-child:input').val()
					// TODO Start animation; show AJAX spinner for element
					t.contents().replaceWith('<em class="ajax">Saving...</em>')
					/*/ post the new value to the server along with its ID
					$.post('save', {id: $editable.attr('id'), value: contents},
						function(data) {
							editable.removeClass('feeActive').contents().replaceWith(contents);
						})*/
					// TODO Stop animation; remove AJAX spinner for element
					t.removeClass('feeActive').contents().replaceWith(contents)
				})
		},
		
		/**
		 * Fee UI - Notifications.
		 */
		notify: {
			
			/**
			 * Show a warning message.
			 */
			warning: function(arg) {
				arg = FEE.$.extend({}, {icon: 'notice', title: 'Warning', sticky: true}, arg)
				Grumble.show(arg)
			},
			
			/**
			 * Show an error message.
			 */
			error: function(arg) {
				arg = FEE.$.extend({}, {icon: 'error', title: 'Error', sticky: true}, arg)
				Grumble.show(arg)
			},
			
			/**
			 * Show a informational message.
			 */
			info: function(arg) {
				arg = FEE.$.extend({}, {icon: 'notice', title: 'Information', sticky: false}, arg)
				Grumble.show(arg)
			},
			
			/**
			 * Show a success message.
			 */
			success: function(arg) {
				arg = FEE.$.extend({}, {icon: 'success', title: 'Success', sticky: false}, arg)
				Grumble.show(arg)
			}
			
		}
		
	}
	
}

//
// Build jQuery plugin
//
var __fee = (function($) {
	var x = {
		findNext: function(t) {
			// Find next element of this type
			var tpn = t.parent().next()
			var ne = tpn.find("input")
			while (!ne[0]) {
				ne = tpn.next().find("input") // TODO t.tagName!?
			}
			var r
			if (ne && ne[0]) r = ne[0]
			return r
		},
		
		textField: function(t) {
			var id = t.attr('id')
			var f$ = t.f$
			f$.d("textField")
			// Bind event
			t.keydown(function(e) {
					/* Special case: return
					if (e.keyCode == 13) {
						e.preventDefault()
						return false
					}*/
					// Commit
					FEE.$.each(f$.opts.commit.keyCodeTrigger, function(i, v) {
						if (e.keyCode == v) {
							f$.commit()
						}
					})
					// Rollback
					FEE.$.each(f$.opts.rollback.keyCodeTrigger, function(i, v) {
						if (e.keyCode == v) {
							f$.rollback()
						}
					})
				})
		},
		
		radioButton: function(t) {
			var id = t.attr('id')
			var f$ = t.f$
			f$.d("radioButton")
			// Bind event
			t.click(function(e) { f$.commit() })
		},
	}
	// Add function fee to jQuery
	$.fn.fee = function(options) {
		// No options given?
		if (!options) {
			options = {}
		}
		// TODO Generate unique group ID if not given in options
		if (!options.groupId) {
			options.groupId = FEE.randomUUID()
			FEE.d(null, "generated group ID=" + options.groupId)
		}
		// For each element returned by selector
		return this.each(function() {
			// 'jQuery' the element
			var t = $(this)
			// Check if we have an id, we just can't live without
			if (!this.id) {
				FEE.w(null, "found element without id:")
				FEE.w(null, this)
				return t
			} else {
				FEE.d(this.id, "fee-ifying element")
				// Attach Fee instance for this element
				var effd = new FEE$(options, '#' + this.id)
				t.extend({
						f$: effd
					})
				t.data.f$ = effd
				// Add methods and bind events depending on element's class
				switch (t.attr('class')) {
					case 'feeTextField': x.textField(t); break
					case 'feeRadioButton': x.radioButton(t); break
				}
				return t
			}
		})
	}
})(jQuery)
