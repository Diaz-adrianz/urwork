import mongoose from 'mongoose';
import paginate from 'mongoose-paginate-v2';

const { model, Schema } = mongoose;

const schema = new Schema(
	{
		title: { type: String, maxlength: 50, required: true },
		project_id: { type: Schema.Types.ObjectId, ref: 'projects' },
		label: {
			type: String,
			enum: ['red', 'yellow', 'blue'],
			default: 'blue',
		},
		completed_date: { type: Date, default: '' },
		completed_by: { type: Schema.Types.ObjectId, ref: 'users' },
	},
	{
		timestamps: true,
	}
);

schema.plugin(paginate);
const TASKS = model('tasks', schema);

export default TASKS;
